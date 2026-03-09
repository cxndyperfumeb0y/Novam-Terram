package net.candyperfumeboy.novamterram.entities.custom;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

/**
 * Songbird Model for Novam Terram.
 * Handles ground hopping, high-frequency flight, and perching logic for Iron Golems.
 */
public class SongbirdModel<T extends Entity> extends HierarchicalModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
            ResourceLocation.fromNamespaceAndPath("novamterram", "songbird"), "main");

    private final ModelPart root;
    private final ModelPart body;
    private final ModelPart head;
    private final ModelPart wing_left;
    private final ModelPart wing_right;
    private final ModelPart tail;
    private final ModelPart leg_left;
    private final ModelPart leg_right;

    public SongbirdModel(ModelPart root) {
        this.root = root.getChild("root");
        this.body = this.root.getChild("body");
        this.head = this.body.getChild("head");
        this.wing_left = this.body.getChild("wing_left");
        this.wing_right = this.body.getChild("wing_right");
        this.tail = this.body.getChild("tail");
        this.leg_left = this.body.getChild("leg_left");
        this.leg_right = this.body.getChild("leg_right");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition root = partdefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        // Body rotated slightly up (0.2618 radians = 15 degrees)
        body.addOrReplaceChild("body_r1", CubeListBuilder.create().texOffs(0, 0)
                        .addBox(-1.5F, -1.5F, -2.0F, 3.0F, 3.0F, 4.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(0.0F, -3.5F, 0.0F, 0.2618F, 0.0F, 0.0F));

        // Head contains the Beak
        body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 7)
                        .addBox(-1.5F, -3.0F, -1.5F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F))
                        .texOffs(14, 14).addBox(-0.5F, -2.0F, 1.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, -3.5F, 2.0F));

        // Wings
        body.addOrReplaceChild("wing_left", CubeListBuilder.create().texOffs(12, 7)
                        .addBox(-1.0F, -1.5F, -2.0F, 1.0F, 3.0F, 4.0F, new CubeDeformation(0.0F)),
                PartPose.offset(-1.5F, -4.0F, 0.0F));

        body.addOrReplaceChild("wing_right", CubeListBuilder.create().texOffs(0, 13)
                        .addBox(0.0F, -1.5F, -2.0F, 1.0F, 3.0F, 4.0F, new CubeDeformation(0.0F)),
                PartPose.offset(1.5F, -4.0F, 0.0F));

        // Tail
        PartDefinition tail = body.addOrReplaceChild("tail", CubeListBuilder.create(), PartPose.offset(0.0F, -3.5F, -2.0F));
        tail.addOrReplaceChild("tail_r1", CubeListBuilder.create().texOffs(14, 0)
                        .addBox(-1.5F, -0.5F, -3.0F, 3.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.1745F, 0.0F, 0.0F));

        // Legs
        body.addOrReplaceChild("leg_left", CubeListBuilder.create().texOffs(14, 4)
                        .addBox(-0.5F, 0.0F, -0.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)),
                PartPose.offset(-1.0F, -2.0F, 0.0F));

        body.addOrReplaceChild("leg_right", CubeListBuilder.create().texOffs(10, 14)
                        .addBox(-0.5F, 0.0F, -0.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)),
                PartPose.offset(1.0F, -2.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 32, 32);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);

        // 1. HEAD MOVEMENT (Always active)
        this.head.yRot = netHeadYaw * ((float)Math.PI / 180F);
        this.head.xRot = headPitch * ((float)Math.PI / 180F);

        // Determine state: Is the bird in the air AND not hitching a ride?
        boolean isFlying = !entity.onGround() && !entity.isPassenger();

        if (!isFlying) {
            // --- GROUNDED / PERCHED STATE ---

            // 2. IDLE TAIL BOB
            this.tail.xRot = (Mth.cos(ageInTicks * 0.2F) * 0.1F);

            // 3. SNAPPY HEAD TWITCH (Idle personality)
            if (Mth.sin(ageInTicks * 0.1F) > 0.8F) {
                this.head.zRot = Mth.cos(ageInTicks * 0.8F) * 0.2F;
            }

            // 4. HOPPING (Only if moving on ground, not while perching on Golem)
            if (limbSwingAmount > 0.1F && !entity.isPassenger()) {
                this.root.y = 24.0F + (Mth.cos(limbSwing * 2.5F) * 1.2F * limbSwingAmount);
                this.body.xRot = -0.25F; // Tilt forward while hopping
            }

            // 5. BREATHING / WING FOLD
            float breath = Mth.sin(ageInTicks * 0.15F) * 0.04F;
            this.wing_left.zRot = -0.05F - breath;
            this.wing_right.zRot = 0.05F + breath;

        } else {
            // --- FLIGHT STATE ---

            // 6. FAST WING FLAP
            float flap = Mth.sin(ageInTicks * 1.3F) * 1.4F;
            this.wing_left.zRot = -flap;
            this.wing_right.zRot = flap;

            // 7. FLIGHT POSTURE (Lean and hover bob)
            this.body.xRot = 0.25F;
            this.root.y = 22.0F + (Mth.sin(ageInTicks * 0.2F) * 0.5F);
            this.tail.xRot = 0.15F;
        }
    }

    @Override
    public ModelPart root() {
        return this.root;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
        if (this.young) {
            poseStack.pushPose();
            // Scale to 70% (Fledgling size)
            poseStack.scale(0.7F, 0.7F, 0.7F);
            // Push down so feet remain at the bottom of the bounding box
            poseStack.translate(0.0D, 0.64D, 0.0D);
            root.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
            poseStack.popPose();
        } else {
            root.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        }
    }
}