package net.candyperfumeboy.novamterram.mixin;

import net.minecraft.client.model.AgeableListModel;
import net.minecraft.client.model.OcelotModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Ocelot;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(OcelotModel.class)
public abstract class OcelotModelMixin<T extends Entity> {

    @Shadow @Final private ModelPart body;
    @Shadow @Final private ModelPart leftHindLeg;
    @Shadow @Final private ModelPart rightHindLeg;
    @Shadow @Final private ModelPart leftFrontLeg;
    @Shadow @Final private ModelPart rightFrontLeg;
    @Shadow @Final private ModelPart tail1;
    @Shadow @Final private ModelPart tail2;

    @Inject(method = "setupAnim", at = @At("TAIL"))
    public void novamterram$crouchAnimation(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo ci) {
        if (entity.getPersistentData().getBoolean("IsSitting")) {
            // Lower the body for a "pounce" look
            this.body.y = 16.0F;
            // Curve the tail down
            this.tail1.xRot = 0.8F;
            this.tail2.xRot = 1.1F;
            // The legs will stay in their vanilla positions, creating the "crouch"
        }
    }}