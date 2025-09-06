package ru.minced.client.feature.module.impl.render.particle;

import lombok.Getter;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import ru.minced.client.util.IMinecraft;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class ParticleHandler implements IMinecraft {
    private static final List<Particle> particles = new ArrayList<>();
    private static final Random random = new Random();
    
    private static Vec3d lastPosition = null;

    public static void spawnParticles(float gravityStrength, float spreadStrength, float lifetime, float count, String particleType, MatrixStack matrixStack) {
        if (IMinecraft.nullCheck()) return;

        particles.removeIf(particle -> particle.isDead(lifetime));

        PlayerEntity player = mc.player;
        Vec3d currentPosition = player.getPos();
        
        if (lastPosition == null) {
            lastPosition = currentPosition;
            return;
        }
        
        double distance = lastPosition.distanceTo(currentPosition);

        double deltaX = Math.abs(currentPosition.x - lastPosition.x);
        double deltaZ = Math.abs(currentPosition.z - lastPosition.z);
        boolean movingHorizontally = deltaX > 0.01 || deltaZ > 0.01;

        if (distance >= 0.1f && movingHorizontally) {
            lastPosition = currentPosition;
            
            Box boundingBox = player.getBoundingBox();
            int particleCountToSpawn = (int) count;

            List<String> selectedParticles = parseSelectedParticles(particleType);
            if (selectedParticles.isEmpty()) {
                return;
            }
            
            for (int i = 0; i < particleCountToSpawn; i++) {
                double boxWidth = (boundingBox.maxX - boundingBox.minX) * 0.5f;
                double boxHeight = boundingBox.maxY - boundingBox.minY;
                
                double offsetX = (random.nextFloat() - 0.5f) * boxWidth;
                double offsetY = random.nextFloat() * boxHeight;
                double offsetZ = (random.nextFloat() - 0.5f) * boxWidth;

                float spreadFactor = spreadStrength * 0.0001f;
                double velocityX = (random.nextFloat() - 0.5f) * spreadFactor;
                double velocityY = (random.nextFloat() - 0.5f) * spreadFactor;
                double velocityZ = (random.nextFloat() - 0.5f) * spreadFactor;

                String randomParticleType = selectedParticles.get(random.nextInt(selectedParticles.size()));

                particles.add(new Particle(
                    currentPosition, 
                    offsetX, offsetY, offsetZ,
                    velocityX, velocityY, velocityZ,
                    0.1f,
                    gravityStrength,
                    spreadStrength > 0,
                    System.currentTimeMillis(),
                    lifetime,
                    randomParticleType
                ));
            }

            if (particles.size() > 500) {
                particles.sort(Comparator.comparingLong(Particle::getSpawnTime));

                int toRemove = particles.size() - 500;
                if (toRemove > 0) {
                    for (int i = 0; i < toRemove; i++) {
                        Particle particle = particles.get(i);
                        if (!particle.isForcedDeath()) {
                            particle.setForcedDeath(true);
                            particle.setForcedDeathTime(System.currentTimeMillis());
                        }
                    }
                }
            }
        } else {
            lastPosition = currentPosition;
        }

        for (Particle particle : particles) {
            particle.update();
            updateParticleSize(particle);
        }

        ParticleRenderer.renderParticles(particles, matrixStack);
    }
    
    private static List<String> parseSelectedParticles(String particleType) {
        if (particleType == null || particleType.isEmpty()) {
            return Collections.singletonList("Lightning");
        }

        if (!particleType.contains(",")) {
            return Collections.singletonList(particleType);
        }

        String[] parts = particleType.split(",");
        List<String> result = new ArrayList<>();
        for (String part : parts) {
            String trimmed = part.trim();
            if (!trimmed.isEmpty()) {
                result.add(trimmed);
            }
        }
        
        return result.isEmpty() ? Collections.singletonList("cross.png") : result;
    }
    
    private static void updateParticleSize(Particle particle) {
        if (particle.isForcedDeath()) {
            float fadeTime = (System.currentTimeMillis() - particle.getForcedDeathTime()) / 200.0f;
            particle.size = 0.3f * Math.max(0, 1.0f - fadeTime);
            return;
        }
        
        float age = (System.currentTimeMillis() - particle.spawnTime) / 1000f;
        
        if (age < 0.2f) {
            particle.size = 0.1f + (0.3f - 0.1f) * (age / 0.2f);
        } else if (age > particle.lifetime - 0.2f) {
            particle.size = 0.3f * (1 - (age - (particle.lifetime - 0.2f)) / 0.2f);
        } else {
            particle.size = 0.3f;
        }
    }
    
    public static class Particle {
        private final Vec3d basePosition;
        private double offsetX, offsetY, offsetZ;
        private final double velocityX;
        private double velocityY;
        private final double velocityZ;
        @Getter
        private float size;
        private final float gravityStrength;
        private final boolean hasSpread;
        @Getter
        private final long spawnTime;
        private final float lifetime;
        @Getter
        private final String texture;
        @Getter
        private boolean forcedDeath = false;
        @Getter
        private long forcedDeathTime = 0;
        
        public Particle(Vec3d position, 
                       double offsetX, double offsetY, double offsetZ,
                       double vx, double vy, double vz, 
                       float size, float gravityStrength, boolean hasSpread,
                       long spawnTime, float lifetime, String texture) {
            this.basePosition = position;
            this.offsetX = offsetX;
            this.offsetY = offsetY;
            this.offsetZ = offsetZ;
            this.velocityX = vx;
            this.velocityY = vy;
            this.velocityZ = vz;
            this.size = size;
            this.gravityStrength = gravityStrength;
            this.hasSpread = hasSpread;
            this.spawnTime = spawnTime;
            this.lifetime = lifetime;
            this.texture = texture;
        }
        
        public void update() {
            if (hasSpread) {
                offsetX += velocityX;
                offsetY += velocityY;
                offsetZ += velocityZ;

                velocityY -= 0.000005f * gravityStrength;
            }
        }
        
        public boolean isDead(float defaultLifetime) {
            if (forcedDeath) {
                return (System.currentTimeMillis() - forcedDeathTime) > 200;
            }
            return (System.currentTimeMillis() - spawnTime) > lifetime * 1000;
        }
        
        public double getX() {
            return basePosition.x + offsetX;
        }
        public double getY() {
            return basePosition.y + offsetY;
        }
        public double getZ() {
            return basePosition.z + offsetZ;
        }

        public float getAlpha() {
            if (forcedDeath) {
                float fadeTime = (System.currentTimeMillis() - forcedDeathTime) / 200.0f;
                return Math.max(0, Math.min(1.0f, 1.0f - fadeTime));
            }
            
            float age = (System.currentTimeMillis() - spawnTime) / 1000.0f;
            if (age < 0.2f) {
                return Math.max(0, Math.min(1.0f, age / 0.2f));
            } else if (age > lifetime - 0.2f) {
                return Math.max(0, Math.min(1.0f, 1.0f - (age - (lifetime - 0.2f)) / 0.2f));
            }
            return 1.0f;
        }
        
        public void setForcedDeath(boolean forcedDeath) {
            this.forcedDeath = forcedDeath;
        }
        
        public void setForcedDeathTime(long forcedDeathTime) {
            this.forcedDeathTime = forcedDeathTime;
        }

    }
} 