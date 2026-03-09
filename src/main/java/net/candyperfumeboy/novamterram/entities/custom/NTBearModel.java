package net.candyperfumeboy.novamterram.entities.custom;


import net.candyperfumeboy.novamterram.entities.custom.AbstractNTBear;
import net.minecraft.client.model.QuadrupedModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class NTBearModel<T extends AbstractNTBear> extends QuadrupedModel<T> {

    public NTBearModel(ModelPart root) {
        super(root, true, 16.0F, 4.0F, 2.25F, 2.0F, 24);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        // Geometry: Paste your CubeListBuilder logic here!
        // Make sure it matches the PolarBearModel precisely.
        return LayerDefinition.create(mesh, 128, 64);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

        // 1. Standing/Bipedal Logic (Inherited from AbstractNTBear)
        float standing = entity.getStandingAnimationScale(0.0F);
        this.body.xRot = (float) (Math.PI / 2) - (standing * standing) * (float) Math.PI * 0.35F;

        // 2. Sleeping Override
        if (entity.isSleepyTime()) {
            this.head.xRot = 0.5F;
            this.head.zRot = 0.0F;
            this.body.xRot = (float) (Math.PI / 2);
        }

        // 3. Dancing Wobble (The "Fun" Feature)
        if (entity.isDancing()) {
            float dance = Mth.sin(ageInTicks * 0.5F) * 0.2F;
            this.body.zRot = dance;
            this.head.zRot = dance * 1.2F;
        } else {
            this.body.zRot = 0.0F;
            this.head.zRot = 0.0F;
        }

        // 4. Climbing Jitter (The "Utility" Feature)
        if (entity.horizontalCollision && !entity.onGround()) {
            float climbJitter = Mth.sin(ageInTicks * 0.8F) * 0.2F;
            this.rightFrontLeg.xRot = climbJitter;
            this.leftFrontLeg.xRot = -climbJitter;
        }
    }
}