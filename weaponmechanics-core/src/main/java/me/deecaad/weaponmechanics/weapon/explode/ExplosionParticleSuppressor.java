package me.deecaad.weaponmechanics.weapon.explode;

import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerParticle;
import org.bukkit.entity.Player;

import java.util.Set;

public final class ExplosionParticleSuppressor implements PacketListener {

    private static final Set<String> SUPPRESSED_PARTICLES = Set.of(
            "minecraft:smoke",
            "minecraft:explosion_emitter"
    );

    @Override
    public void onPacketSend(PacketSendEvent event) {
        if (event.getPlayer() == null) {
            return;
        }

        String type = String.valueOf(event.getPacketType());
        if (!type.contains("PARTICLE")) {
            return;
        }

        try {
            Player player = event.getPlayer();
            WrapperPlayServerParticle wrapper = new WrapperPlayServerParticle(event);

            var pos = wrapper.getPosition();
            if (!Explosion.shouldSuppressDefaultParticles(
                    player.getWorld().getName(),
                    pos.getX(),
                    pos.getY(),
                    pos.getZ()
            )) {
                return;
            }

            String particleName = wrapper.getParticle().getType().getName().toString();
            if (SUPPRESSED_PARTICLES.contains(particleName)) {
                event.setCancelled(true);
            }
        } catch (Exception ignored) {
            // Ignore malformed or unsupported particle packets.
        }
    }
}