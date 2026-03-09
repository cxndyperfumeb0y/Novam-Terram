package net.candyperfumeboy.novamterram.entities.custom;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

/**
 * Hyena Model - Novam Terram 1.21.1
 * Features:
 * - Connected "Bridge" Snout (No floating gaps)
 * - Anti-Flicker Sitting Logic (Prevents Z-Fighting)
 * - Dynamic Scaling for Adults (0.85x) and Babies (0.45x)
 */
public class HyenaModel<T extends HyenaEntity> extends EntityModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
            ResourceLocation.fromNamespaceAndPath("novamterram", "hyena"), "main");

    // --- MODEL PARTS ---
    private final ModelPart root;
    private final ModelPart bodyFront;
    private final ModelPart bodyBack;
    private final ModelPart neck;
    private final ModelPart head;
    private final ModelPart snout;
    private final ModelPart rightFrontLeg;
    private final ModelPart leftFrontLeg;
    private final ModelPart rightBackLeg;
    private final ModelPart leftBackLeg;
    private final ModelPart tail;

    public HyenaModel(ModelPart root) {
        this.root = root;
        // Navigation matches the Hierarchy in createBodyLayer
        this.bodyFront = root.getChild("body_front");
        this.bodyBack  = this.bodyFront.getChild("body_back");
        this.tail      = this.bodyBack.getChild("tail");
        this.neck      = this.bodyFront.getChild("neck");
        this.head      = this.neck.getChild("head");
        this.snout     = this.head.getChild("snout");

        this.rightFrontLeg = root.getChild("right_front_leg");
        this.leftFrontLeg  = root.getChild("left_front_leg");
        this.rightBackLeg  = root.getChild("right_back_leg");
        this.leftBackLeg   = root.getChild("left_back_leg");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        // --- 1. FRONT BODY (Core Parent) ---
        PartDefinition body_front = partdefinition.addOrReplaceChild("body_front", CubeListBuilder.create(),
                PartPose.offset(0.0F, 11.0F, -4.0F));

        body_front.addOrReplaceChild("mane_ridge", CubeListBuilder.create()
                        .texOffs(36, 22).addBox(-0.5F, -1.5F, -1.0F, 1.0F, 2.0F, 16.0F),
                PartPose.offsetAndRotation(0.0F, -4.0F, 1.0F, -0.0873F, 0.0F, 0.0F));

        body_front.addOrReplaceChild("main_chest", CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-4.0F, -5.0F, 5.0F, 8.0F, 9.0F, 13.0F),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.1309F, 0.0F, 0.0F));

        // --- 2. BACK BODY & TAIL ---
        PartDefinition body_back = body_front.addOrReplaceChild("body_back", CubeListBuilder.create()
                        .texOffs(0, 22).addBox(-3.5F, -5.0F, -13.0F, 7.0F, 8.0F, 11.0F),
                PartPose.offset(0.0F, 1.0F, 9.0F));

        body_back.addOrReplaceChild("tail", CubeListBuilder.create()
                        .texOffs(42, 0).addBox(-1.5F, -4.0F, -2.0F, 3.0F, 3.0F, 7.0F),
                PartPose.offsetAndRotation(0.0F, 3.0F, 8.0F, 0.3491F, 0.0436F, 0.0F));

        // --- 3. NECK & HEAD ---
        PartDefinition neck = body_front.addOrReplaceChild("neck", CubeListBuilder.create(),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        neck.addOrReplaceChild("neck_base", CubeListBuilder.create()
                        .texOffs(0, 41).addBox(-2.5F, -5.0F, -6.0F, 5.0F, 6.0F, 5.0F),
                PartPose.offsetAndRotation(0.0F, -3.0F, 1.0F, 0.6109F, 0.0F, 0.0F));

        PartDefinition head = neck.addOrReplaceChild("head", CubeListBuilder.create()
                        .texOffs(36, 40).addBox(-3.5F, -3.5F, -4.0F, 7.0F, 7.0F, 8.0F),
                PartPose.offset(0.0F, -4.0F, -5.0F));

        // SNOUT: Z-8 with 6 depth ensures it overlaps 2 units into the head (No gaps)
        head.addOrReplaceChild("snout", CubeListBuilder.create()
                        .texOffs(42, 10).addBox(-2.0F, -1.0F, -8.0F, 4.0F, 3.0F, 6.0F),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        head.addOrReplaceChild("ear_right", CubeListBuilder.create()
                        .texOffs(50, 17).addBox(-1.0F, -3.0F, -1.0F, 3.0F, 4.0F, 1.0F),
                PartPose.offsetAndRotation(2.5F, -4.0F, 1.0F, 0.0F, 0.0F, -0.2618F));

        head.addOrReplaceChild("ear_left", CubeListBuilder.create()
                        .texOffs(42, 17).addBox(-2.0F, -3.0F, -1.0F, 3.0F, 4.0F, 1.0F),
                PartPose.offsetAndRotation(-2.5F, -4.0F, 1.0F, 0.0F, 0.0F, 0.2618F));

        // --- 4. LEGS ---
        partdefinition.addOrReplaceChild("right_front_leg", CubeListBuilder.create()
                .texOffs(20, 41).addBox(-1.5F, 0.0F, -1.5F, 3.0F, 12.0F, 3.0F), PartPose.offset(-3.5F, 12.0F, -2.0F));
        partdefinition.addOrReplaceChild("left_front_leg", CubeListBuilder.create()
                .texOffs(0, 52).addBox(-1.5F, 0.0F, -1.5F, 3.0F, 12.0F, 3.0F), PartPose.offset(3.5F, 12.0F, -2.0F));
        partdefinition.addOrReplaceChild("right_back_leg", CubeListBuilder.create()
                .texOffs(32, 55).addBox(-1.5F, 0.0F, -1.5F, 3.0F, 10.0F, 3.0F), PartPose.offset(-3.0F, 14.0F, 9.0F));
        partdefinition.addOrReplaceChild("left_back_leg", CubeListBuilder.create()
                .texOffs(44, 55).addBox(-1.5F, 0.0F, -1.5F, 3.0F, 10.0F, 3.0F), PartPose.offset(3.0F, 14.0F, 9.0F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        // Reset base positions
        this.bodyFront.y = 11.0F;
        this.rightFrontLeg.y = 12.0F;
        this.leftFrontLeg.y = 12.0F;
        this.rightBackLeg.y = 14.0F;
        this.leftBackLeg.y = 14.0F;
        this.neck.y = 0.0F;
        this.neck.z = 0.0F;

        if (entity.isInSittingPose()) {
            this.bodyFront.xRot = -0.4363F;
            this.bodyFront.y = 14.0F;

            // ANTI-FLICKER: Nudges head forward so neck/chest textures don't Z-fight
            this.neck.y = -0.5F;
            this.neck.z = -0.2F;

            this.rightFrontLeg.xRot = -0.1745F;
            this.leftFrontLeg.xRot = -0.1745F;
            this.rightBackLeg.xRot = -(float)Math.PI / 2F;
            this.leftBackLeg.xRot = -(float)Math.PI / 2F;
            this.rightBackLeg.y = 22.0F;
            this.leftBackLeg.y = 22.0F;
        } else {
            this.bodyFront.xRot = 0.0F;
            this.rightFrontLeg.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
            this.leftFrontLeg.xRot  = Mth.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAmount;
            this.rightBackLeg.xRot  = Mth.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAmount;
            this.leftBackLeg.xRot   = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
        }

        // Head looking logic (reduced sensitivity for a heavier neck feel)
        this.head.yRot = netHeadYaw * ((float)Math.PI / 180F) * 0.5F;
        this.head.xRot = headPitch * ((float)Math.PI / 180F) * 0.5F;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
        poseStack.pushPose();

        // SCALING LOGIC
        if (this.young) {
            poseStack.scale(0.45F, 0.45F, 0.45F);
            poseStack.translate(0.0D, 1.85D, 0.0D);
        } else {
            poseStack.scale(0.85F, 0.85F, 0.85F);
            poseStack.translate(0.0D, 0.25D, 0.0D);
        }

        // RENDERING (Body hierarchy starting from root parents)
        this.bodyFront.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        this.rightFrontLeg.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        this.leftFrontLeg.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        this.rightBackLeg.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        this.leftBackLeg.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);

        poseStack.popPose();
    }
}