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
 * FaunModel - Novam Terram 1.21.1
 * * DESIGN SPECIFICATIONS:
 * - 8-wide Body (Standard Deer proportions)
 * - 14-tall Legs (Graceful, high-stepping gait)
 * - Dynamic Antler visibility (Shedding Support)
 * - Freeze Camouflage (AI-Reactive animation)
 */
public class FaunModel<T extends FaunEntity> extends EntityModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
            ResourceLocation.fromNamespaceAndPath("novamterram", "faun"), "main");

    // --- MODEL PARTS ---
    private final ModelPart root;
    private final ModelPart body;
    private final ModelPart neck;
    private final ModelPart head;
    private final ModelPart antlers;
    private final ModelPart leg_fr;
    private final ModelPart leg_fl;
    private final ModelPart leg_br;
    private final ModelPart leg_bl;
    private final ModelPart tail;

    public FaunModel(ModelPart root) {
        this.root = root.getChild("root");
        this.body = this.root.getChild("body");
        this.tail = this.body.getChild("tail_r1");
        this.neck = this.body.getChild("neck");
        this.head = this.neck.getChild("head");
        this.antlers = this.head.getChild("antlers");

        this.leg_fr = this.root.getChild("leg_fr");
        this.leg_fl = this.root.getChild("leg_fl");
        this.leg_br = this.root.getChild("leg_br");
        this.leg_bl = this.root.getChild("leg_bl");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        // --- ROOT HIERARCHY ---
        PartDefinition root = partdefinition.addOrReplaceChild("root",
                CubeListBuilder.create(),
                PartPose.offset(0.0F, 24.0F, 0.0F));

        // --- BODY & TAIL ---
        PartDefinition body = root.addOrReplaceChild("body",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(-4.0F, -7.0F, -7.0F, 8.0F, 9.0F, 14.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, -14.0F, 0.0F));

        body.addOrReplaceChild("tail_r1",
                CubeListBuilder.create()
                        .texOffs(44, 7)
                        .addBox(-1.0F, -4.0F, 0.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(0.0F, -5.0F, 7.0F, -0.6981F, 0.0436F, 0.0F));

        // --- NECK & HEAD ---
        PartDefinition neck = body.addOrReplaceChild("neck",
                CubeListBuilder.create()
                        .texOffs(26, 23)
                        .addBox(-2.0F, -9.0F, -2.0F, 4.0F, 11.0F, 5.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(0.0F, -5.0F, -6.0F, 0.3491F, 0.0F, 0.0F));

        PartDefinition head = neck.addOrReplaceChild("head",
                CubeListBuilder.create()
                        .texOffs(0, 23)
                        .addBox(-3.0F, -6.0F, -3.0F, 6.0F, 6.0F, 7.0F, new CubeDeformation(0.0F))
                        .texOffs(44, 0)
                        .addBox(-2.0F, -3.0F, -6.0F, 4.0F, 3.0F, 4.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(0.0F, -8.0F, -1.0F, -0.3491F, 0.0F, 0.0F));

        // --- EARS & ANTLERS ---
        head.addOrReplaceChild("ear_left_r1",
                CubeListBuilder.create()
                        .texOffs(44, 18)
                        .addBox(0.0F, -2.0F, 0.0F, 4.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(3.0F, -4.0F, 0.0F, 0.0F, 0.0F, -0.5236F));

        head.addOrReplaceChild("ear_right_r1",
                CubeListBuilder.create()
                        .texOffs(44, 15)
                        .addBox(-4.0F, -2.0F, 0.0F, 4.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-3.0F, -4.0F, 0.0F, 0.0F, 0.0F, 0.5236F));

        PartDefinition antlers = head.addOrReplaceChild("antlers",
                CubeListBuilder.create(),
                PartPose.offset(0.0F, -5.0F, 1.0F));

        antlers.addOrReplaceChild("left_tine_r1",
                CubeListBuilder.create()
                        .texOffs(48, 26).addBox(-0.5F, 0.0F, -0.5F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(48, 21).addBox(-3.5F, 0.0F, -0.5F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(1.5F, -4.0F, -2.5F, 0.3491F, 0.0F, 0.0F));

        antlers.addOrReplaceChild("left_main_r1",
                CubeListBuilder.create()
                        .texOffs(44, 29).addBox(-0.5F, -6.0F, -0.5F, 1.0F, 7.0F, 1.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(1.5F, -1.0F, -0.5F, 0.0F, 0.0F, 0.2618F));

        antlers.addOrReplaceChild("right_main_r1",
                CubeListBuilder.create()
                        .texOffs(44, 21).addBox(-0.5F, -6.0F, -0.5F, 1.0F, 7.0F, 1.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-1.5F, -1.0F, -0.5F, 0.0F, 0.0F, -0.2618F));

        // --- QUADRUPED LEGS (Symmetrically Positioned) ---
        root.addOrReplaceChild("leg_fr",
                CubeListBuilder.create().texOffs(0, 36).addBox(-1.5F, 0.0F, -1.5F, 3.0F, 14.0F, 3.0F),
                PartPose.offset(2.5F, -14.0F, -5.0F));

        root.addOrReplaceChild("leg_fl",
                CubeListBuilder.create().texOffs(12, 36).addBox(-1.5F, 0.0F, -1.5F, 3.0F, 14.0F, 3.0F),
                PartPose.offset(-2.5F, -14.0F, -5.0F));

        root.addOrReplaceChild("leg_br",
                CubeListBuilder.create().texOffs(24, 39).addBox(-1.5F, 0.0F, -1.5F, 3.0F, 14.0F, 3.0F),
                PartPose.offset(2.5F, -14.0F, 5.0F));

        root.addOrReplaceChild("leg_bl",
                CubeListBuilder.create().texOffs(36, 39).addBox(-1.5F, 0.0F, -1.5F, 3.0F, 14.0F, 3.0F),
                PartPose.offset(-2.5F, -14.0F, 5.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        // --- 1. DYNAMIC VISIBILITY (ANTLER SHEDDING) ---
        // This line checks the Boolean we added to FaunEntity to hide antlers when shed.
        this.antlers.visible = !entity.isBaby() && entity.isBuck() && entity.hasAntlers();

        // --- 2. FREEZE CAMOUFLAGE MODIFIER ---
        // If the entity is currently "freezing" (hiding), we kill the animation movement.
        float animMultiplier = entity.isHiding() ? 0.05F : 1.0F;
        float modifiedLimbSwingAmount = limbSwingAmount * animMultiplier;

        // --- 3. HEAD & NECK TRACKING ---
        // Movement is dampened if freezing to look more natural.
        this.head.yRot = (netHeadYaw * ((float)Math.PI / 180F)) * animMultiplier;
        this.head.xRot = ((headPitch * ((float)Math.PI / 180F)) - 0.3491F) * animMultiplier;

        // --- 4. QUADRUPED LOCOMOTION ---
        float speed = 0.6662F;
        float degree = 1.4F; // Slightly higher degree for a deer's high-step

        // Front Legs Animation
        this.leg_fr.xRot = Mth.cos(limbSwing * speed) * degree * modifiedLimbSwingAmount;
        this.leg_fl.xRot = Mth.cos(limbSwing * speed + (float)Math.PI) * degree * modifiedLimbSwingAmount;

        // Back Legs Animation
        this.leg_br.xRot = Mth.cos(limbSwing * speed + (float)Math.PI) * degree * modifiedLimbSwingAmount;
        this.leg_bl.xRot = Mth.cos(limbSwing * speed) * degree * modifiedLimbSwingAmount;

        // --- 5. IDLE ANIMATIONS ---
        // Tail flicking slightly in the wind or idle.
        this.tail.zRot = Mth.cos(ageInTicks * 0.1F) * 0.15F;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
        if (this.young) {
            poseStack.pushPose();
            // Scale baby Fauns to half size and adjust position to keep feet on ground
            poseStack.scale(0.5F, 0.5F, 0.5F);
            poseStack.translate(0.0D, 1.5D, 0.0D);
            this.root.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
            poseStack.popPose();
        } else {
            this.root.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        }
    }
}