package ru.minced.client.feature.module.impl.render;

import ru.minced.client.core.event.EventHandler;
import ru.minced.client.core.event.impl.render.EventWorld;
import ru.minced.client.feature.module.Category;
import ru.minced.client.feature.module.Module;
import ru.minced.client.feature.module.impl.render.particle.ParticleHandler;
import ru.minced.client.feature.module.setting.impl.ModeListSetting;
import ru.minced.client.feature.module.setting.impl.SliderSetting;

public class ParticleModule extends Module {
    private final SliderSetting gravityStrength = new SliderSetting("Gravity Strength", 0.0f, -1.0f, 1.0f, 0.1f);
    private final SliderSetting spreadStrength = new SliderSetting("Spread Strength", 5.0f, 1.0f, 10.0f, 0.1f);
    private final SliderSetting lifetime = new SliderSetting("Lifetime", 2.0f, 0.5f, 5.0f, 0.1f);
    private final SliderSetting particleCount = new SliderSetting("Particle Count", 3.0f, 1.0f, 5.0f, 0.5f);
    private final ModeListSetting particles = new ModeListSetting("Particles", 
            "Cross", "Heart", "SnowFlake", "Star",
            "Crown", "Lightning", "Rhombus", "Triangle",
            "Dollar", "Line", "Point");
    
    public ParticleModule() {
        super("Particle", "Spawns particles behind the player", Category.Visuals);
        addSettings(gravityStrength, spreadStrength, lifetime, particleCount, particles);
    }
    
    @EventHandler
    public void onRender(EventWorld event) {
        if (isState()) {
            String selectedParticles = particles.getSelected().isEmpty() 
                ? particles.getList().getFirst()
                : String.join(",", particles.getSelected());
            
            ParticleHandler.spawnParticles(
                gravityStrength.getValue(),
                spreadStrength.getValue(),
                lifetime.getValue(),
                particleCount.getValue(),
                selectedParticles,
                event.getStack()
            );
        }
    }
} 