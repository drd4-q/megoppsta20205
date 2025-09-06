package ru.minced.mixin.accessor;

import com.mojang.authlib.minecraft.UserApiService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.SocialInteractionsManager;
import net.minecraft.client.session.ProfileKeys;
import net.minecraft.client.session.Session;
import net.minecraft.client.session.report.AbuseReportContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MinecraftClient.class)
public interface MinecraftAccessor {

    @Mutable
    @Accessor("session")
    void setSession(Session session);

    @Accessor("itemUseCooldown")
    int getItemUseCooldown();

    @Accessor("itemUseCooldown")
    void setItemUseCooldown(int val);

    @Mutable
    @Accessor
    void setUserApiService(UserApiService apiService);

    @Mutable
    @Accessor("profileKeys")
    void setProfileKeys(ProfileKeys keys);

    @Mutable
    @Accessor("socialInteractionsManager")
    void setSocialInteractionsManager(SocialInteractionsManager socialInteractionsManager);

    @Mutable
    @Accessor("abuseReportContext")
    void setAbuseReportContext(AbuseReportContext abuseReportContext);

    @Accessor("authenticationService")
    YggdrasilAuthenticationService getAuthenticationService();
}
