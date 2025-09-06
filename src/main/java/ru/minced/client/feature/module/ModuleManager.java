package ru.minced.client.feature.module;

import ru.minced.client.feature.module.impl.client.*;
import ru.minced.client.feature.module.impl.fight.*;
import ru.minced.client.feature.module.impl.miscellaneous.*;
import ru.minced.client.feature.module.impl.movement.*;
import ru.minced.client.feature.module.impl.player.*;
import ru.minced.client.feature.module.impl.render.*;
import ru.minced.client.feature.module.impl.client.*;
import ru.minced.client.feature.module.impl.fight.*;
import ru.minced.client.feature.module.impl.miscellaneous.*;
import ru.minced.client.feature.module.impl.movement.*;
import ru.minced.client.feature.module.impl.player.*;
import ru.minced.client.feature.module.impl.render.*;
import ru.minced.client.feature.module.impl.world.AFKModule;
import ru.minced.client.feature.module.impl.world.AmbienceModule;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Getter
public class ModuleManager {
    public static List<Module> modules = new ArrayList<>();

    private SprintModule sprintModule;
    private ScreenWalkModule screenWalkModule;
    private SpeedModule speedModule;
    private TwerkModule twerkModule;
    private NoRenderModule noRenderModule;
    private NameProtectModule nameProtectModule;
    private AmbienceModule ambienceModule;
    private TrueSightModule trueSightModule;
    private DisplayModule displayModule;
    private EntityESPModule entityESPModule;
    private TracersModule tracersModule;
    private ClickGuiModule clickGuiModule;
    private CameraModule cameraModule;
    private DeathScreenModule deathScreenModule;
    private NightVisionModule nightVisionModule;
    private TriggerModule triggerModule;
    private NoEntityTraceModule noEntityTraceModule;
    private NoDamageModule noDamageModule;
    private AttackAuraModule attackAuraModule;
    private CriticalsModule criticalsModule;
    private CTLeaveModule ctLeaveModule;
    private NoDelayModule noDelayModule;
    private AutoRevivalModule autoRevivalModule;
    private DeathCoordinatesModule deathCoordinatesModule;
    private FreezeModule freezeModule;
    private BindSwapModule bindSwapModule;
    private NoPushModule noPushModule;
    private EffectCancelModule effectCancelModule;
    private SoundsModule soundsModule;
    private ArrowsModule arrowsModule;
    private AFKModule afkModule;
    private ItemESPModule itemESPModule;
    private NoSlowModule noSlowModule;
    private TargetESPModule targetESPModule;
    private AntiAimModule antiAimModule;
    private AspectModule aspectModule;
    private NoWebModule noWebModule;
    private ThemeModule themeModule;
    private ElytraHelperModule elytraHelperModule;
    private NameTagsModule nameTagsModule;
    private ParticleModule particleModule;
    private AutoTotemModule autoTotemModule;
    private NoSlotChangeModule noSlotChangeModule;
    private AntiBotModule antiBotModule;
    private PearlPredictionModule pearlPredictionModule;
    private ClickPearlModule clickPearlModule;
    private ClickFriendModule clickFriendModule;
    private BrandSpoofModule brandSpoofModule;
    private ItemPhysicModule itemPhysicModule;
    private ItemScrollerModule itemScrollerModule;
    private RPSpoofModule rpSpoofModule;
    private NoHeadPlaceModule noHeadPlaceModule;
    private TNTTimerModule tntTimerModule;
    private ViewModelModule viewModelModule;
    private AutoGGModule autoGGModule;
    private VelocityModule velocityModule;
    private FakeLagModule fakeLagModule;
    private ReachModule reachModule;
    private FreeCamModule freeCamModule;
    private FlyModule flyModule;
    private LiquidWalkModule liquidWalkModule;
    private MaceKillModule maceKillModule;

    public void init() {
        addAll(
                sprintModule = new SprintModule(),
                screenWalkModule = new ScreenWalkModule(),
                noRenderModule = new NoRenderModule(),
                triggerModule = new TriggerModule(),
                nameProtectModule = new NameProtectModule(),
                ambienceModule = new AmbienceModule(),
                ctLeaveModule = new CTLeaveModule(),
                noDelayModule = new NoDelayModule(),
                noEntityTraceModule = new NoEntityTraceModule(),
                soundsModule = new SoundsModule(),
                trueSightModule = new TrueSightModule(),
                autoRevivalModule = new AutoRevivalModule(),
                displayModule = new DisplayModule(),
                deathCoordinatesModule = new DeathCoordinatesModule(),
                freezeModule = new FreezeModule(),
                entityESPModule = new EntityESPModule(),
                noDamageModule = new NoDamageModule(),
                tracersModule = new TracersModule(),
                clickGuiModule = new ClickGuiModule(),
                cameraModule = new CameraModule(),
                attackAuraModule = new AttackAuraModule(),
                bindSwapModule = new BindSwapModule(),
                noPushModule = new NoPushModule(),
                deathScreenModule = new DeathScreenModule(),
                speedModule = new SpeedModule(),
                effectCancelModule = new EffectCancelModule(),
                twerkModule = new TwerkModule(),
                arrowsModule = new ArrowsModule(),
                criticalsModule = new CriticalsModule(),
                nightVisionModule = new NightVisionModule(),
                afkModule = new AFKModule(),
                itemESPModule = new ItemESPModule(),
                noSlowModule = new NoSlowModule(),
                targetESPModule = new TargetESPModule(),
                antiAimModule = new AntiAimModule(),
                aspectModule = new AspectModule(),
                noWebModule = new NoWebModule(),
                themeModule = new ThemeModule(),
                elytraHelperModule = new ElytraHelperModule(),
                nameTagsModule = new NameTagsModule(),
                particleModule = new ParticleModule(),
                autoTotemModule = new AutoTotemModule(),
                noSlotChangeModule = new NoSlotChangeModule(),
                antiBotModule = new AntiBotModule(),
                pearlPredictionModule = new PearlPredictionModule(),
                clickPearlModule = new ClickPearlModule(),
                clickFriendModule = new ClickFriendModule(),
                brandSpoofModule = new BrandSpoofModule(),
                itemPhysicModule = new ItemPhysicModule(),
                itemScrollerModule = new ItemScrollerModule(),
                rpSpoofModule = new RPSpoofModule(),
                noHeadPlaceModule = new NoHeadPlaceModule(),
                tntTimerModule = new TNTTimerModule(),
                viewModelModule = new ViewModelModule(),
                autoGGModule = new AutoGGModule(),
                velocityModule = new VelocityModule(),
                fakeLagModule = new FakeLagModule(),
                reachModule = new ReachModule(),
                freeCamModule = new FreeCamModule(),
                flyModule = new FlyModule(),
                liquidWalkModule = new LiquidWalkModule(),
                maceKillModule = new MaceKillModule()
        );
        sortModules();
    }

    public void addAll(Module... module) {
        modules.addAll(List.of(module));
    }

    public static Module get(String name) {
        for (Module module : modules) {
            if (!module.getName().equalsIgnoreCase(name)) continue;
            return module;
        }
        return null;
    }

    private void sortModules() {
        modules.sort(Comparator.comparing(Module::getName));
    }

    public List<Module> modules() {
        return modules;
    }
}
