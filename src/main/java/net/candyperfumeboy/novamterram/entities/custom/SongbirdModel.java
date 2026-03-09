package net.candyperfumeboy.novamterram.entities.custom;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

public class SongbirdModel<T extends Entity> extends EntityModel<T> {
    private final ModelPart root;
    private final ModelPart body;
    private final ModelPart head;
    private final ModelPart leftWing;
    private final ModelPart rightWing;

    public SongbirdModel(ModelPart root) {
        this.root = root;
        // Navigation: Root -> Body -> Children
        this.body = root.getChild("body");
        this.head = this.body.getChild("head");
        this.leftWing = this.body.getChild("left_wing");
        this.rightWing = this.body.getChild("right_wing");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        // 1. MAIN BODY
        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0)
                        .addBox(-1.0F, -4.0F, -2.5F, 2.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)) // Core
                        .texOffs(14, 2).addBox(-1.5F, -2.0F, 0.0F, 1.0F, 2.0F, 0.0F, new CubeDeformation(0.0F))  // Leg L
                        .texOffs(14, 4).addBox(0.5F, -2.0F, 0.0F, 1.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), // Leg R
                PartPose.offset(0.0F, 24.0F, 0.0F));

        // 2. WINGS (Separated for animation)
        body.addOrReplaceChild("left_wing", CubeListBuilder.create().texOffs(0, 12)
                        .addBox(-1.0F, 0.0F, -1.5F, 1.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)),
                PartPose.offset(-1.0F, -4.0F, 0.0F));

        body.addOrReplaceChild("right_wing", CubeListBuilder.create().texOffs(12, 7)
                        .addBox(0.0F, 0.0F, -1.5F, 1.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)),
                PartPose.offset(1.0F, -4.0F, 0.0F));

        // 3. TAIL
        body.addOrReplaceChild("tail_r1", CubeListBuilder.create().texOffs(0, 7)
                        .addBox(-1.0F, 0.0F, 0.0F, 2.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(0.0F, -3.5F, 2.5F, 0.2618F, 0.0F, 0.0F));

        // 4. HEAD
        PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(10, 13)
                        .addBox(-1.0F, -2.0F, -2.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)) // Head
                        .texOffs(14, 0).addBox(-0.5F, -1.0F, -3.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), // Beak
                PartPose.offset(0.0F, -3.0F, -2.5F));

        return LayerDefinition.create(meshdefinition, 32, 32);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        // 1. BASE HEAD ROTATION (Player tracking)
        this.head.xRot = headPitch * ((float)Math.PI / 180F);
        this.head.yRot = netHeadYaw * ((float)Math.PI / 180F);

        // Default positions
        this.leftWing.zRot = 0.0F;
        this.rightWing.zRot = 0.0F;
        this.body.xRot = 0.0F;

        // 2. IDLE & MOVEMENT LOGIC
        if (entity.onGround()) {
            // IDLE: TAIL BOBBING (Birds constantly balance their tail)
            // A quick, short sine wave
            this.body.getChild("tail_r1").xRot = 0.2618F + (Mth.cos(ageInTicks * 0.2F) * 0.05F);

            // IDLE: HEAD TWITCHING
            // Small birds don't move their heads smoothly; they "snap" to positions.
            // We use a stepped sine wave to simulate a "glitchy" bird look.
            float headSnap = Mth.sin(ageInTicks * 0.1F);
            if (headSnap > 0.7F) {
                this.head.zRot = Mth.cos(ageInTicks * 0.8F) * 0.1F; // Tilt head quickly
            } else {
                this.head.zRot = 0.0F;
            }

            // BREATHING (Wings subtly move out and in)
            this.leftWing.zRot = -0.05F - (Mth.sin(ageInTicks * 0.1F) * 0.02F);
            this.rightWing.zRot = 0.05F + (Mth.sin(ageInTicks * 0.1F) * 0.02F);

            // WALKING (Hop effect)
            if (limbSwingAmount > 0.1) {
                this.body.y = 24.0F + (Mth.cos(limbSwing * 1.5F) * 0.5F * limbSwingAmount); // Higher frequency "hop"
                this.body.xRot = -0.1F; // Tilt forward while hopping
            } else {
                this.body.y = 24.0F;
            }

        } else {
            // FLYING: FLAP ANIMATION
            // High frequency (0.8F) for small songbird wings
            float flap = Mth.sin(ageInTicks * 0.8F) * 1.1F;
            this.leftWing.zRot = -flap;
            this.rightWing.zRot = flap;

            // Tilt the body forward and move it up/down slightly in the air
            this.body.xRot = 0.15F;
            this.body.y = 23.0F + (Mth.sin(ageInTicks * 0.2F) * 0.2F);

            // Tail spreads or flattens slightly during flight
            this.body.getChild("tail_r1").xRot = 0.1F;
        }
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
        root.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
    }
}