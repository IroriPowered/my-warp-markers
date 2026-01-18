package cc.irori.mywarpmarkers;

import com.hypixel.hytale.builtin.teleport.TeleportPlugin;
import com.hypixel.hytale.builtin.teleport.Warp;
import com.hypixel.hytale.protocol.packets.worldmap.MapMarker;
import com.hypixel.hytale.server.core.asset.type.gameplay.GameplayConfig;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.WorldMapTracker;
import com.hypixel.hytale.server.core.universe.world.events.AddWorldEvent;
import com.hypixel.hytale.server.core.universe.world.worldmap.WorldMapManager;
import com.hypixel.hytale.server.core.util.PositionUtil;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import java.util.Map;import java.util.regex.Pattern;

public class MyWarpMarkers extends JavaPlugin {

    public MyWarpMarkers(@NonNullDecl JavaPluginInit init) {
        super(init);
    }

    @Override
    protected void start() {
        for (World world : Universe.get().getWorlds().values()) {
            setupForWorld(world);
        }

        getEventRegistry().registerGlobal(AddWorldEvent.class, event -> setupForWorld(event.getWorld()));
    }

    @Override
    protected void shutdown() {
        for (World world : Universe.get().getWorlds().values()) {
            restoreDefaultForWorld(world);
        }
    }

    private static void setupForWorld(World world) {
        world.getWorldMapManager().addMarkerProvider("warps", MyWarpMarkerProvider.INSTANCE);
    }

    private static void restoreDefaultForWorld(World world) {
        world.getWorldMapManager().addMarkerProvider("warps", TeleportPlugin.WarpMarkerProvider.INSTANCE);
    }

    private static class MyWarpMarkerProvider implements WorldMapManager.MarkerProvider {

        private static final MyWarpMarkerProvider INSTANCE = new MyWarpMarkerProvider();

        @Override
        public void update(World world, GameplayConfig gameplayConfig, WorldMapTracker tracker, int chunkViewRadius, int playerChunkX, int playerChunkZ) {
            Map<String, Warp> warps = TeleportPlugin.get().getWarps();
            if (warps.isEmpty() || !gameplayConfig.getWorldMapConfig().isDisplayWarps()) {
                return;
            }

            Player player = tracker.getPlayer();
            Pattern pattern = Pattern.compile("\\b" + Pattern.quote(player.getDisplayName()) + "\\b", Pattern.CASE_INSENSITIVE);

            for(Warp warp : warps.values()) {
                if (!warp.getWorld().equals(world.getName())) {
                    continue;
                }
                if (warp.getId().equalsIgnoreCase(player.getDisplayName()) || pattern.matcher(warp.getId()).find()) {
                    tracker.trySendMarker(
                            chunkViewRadius,
                            playerChunkX,
                            playerChunkZ,
                            warp.getTransform().getPosition(),
                            warp.getTransform().getRotation().getYaw(),
                            "Warp-" + warp.getId(),
                            "Warp: " + warp.getId(),
                            warp,
                            (id, name, w) -> new MapMarker(
                                    id,
                                    name,
                                    "Warp.png",
                                    PositionUtil.toTransformPacket(w.getTransform()),
                                    null
                            )
                    );
                }
            }
        }
    }
}
