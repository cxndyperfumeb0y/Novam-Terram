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
import net.minecraft.world.entity.Entity;

public class FaunModel<T extends Entity> extends EntityModel<T> {
    private final ModelPart root;
    private final ModelPart body;
    private final ModelPart head;
    private final ModelPart leg_back_left;
    private final ModelPart leg_back_right;
    private final ModelPart leg_front_left;
    private final ModelPart leg_front_right;
    private final ModelPart tail;

    public FaunModel(ModelPart root) {
        this.root = root;
        // Strict Hierarchy: root -> body -> [legs, neck, tail]
        this.body = root.getChild("body");
        this.leg_back_left = this.body.getChild("leg_back_left");
        this.leg_back_right = this.body.getChild("leg_back_right");
        this.leg_front_left = this.body.getChild("leg_front_left");
        this.leg_front_right = this.body.getChild("leg_front_right");
        this.tail = this.body.getChild("tail");

        // Deeper path: body -> neck -> head
        ModelPart neck = this.body.getChild("neck");
        this.head = neck.getChild("head");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        // 1. BODY (The anchor for everything)
        // Offset 14.0 puts it at the correct height for a deer-like creature
        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0)
                        .addBox(-3.0F, -3.0F, -6.0F, 6.0F, 6.0F, 13.0F),
                PartPose.offset(0.0F, 14.0F, 0.0F));

        // 2. NECK & HEAD (Parented to body)
        PartDefinition neck = body.addOrReplaceChild("neck", CubeListBuilder.create().texOffs(20, 19)
                        .addBox(-2.0F, -6.0F, -1.5F, 4.0F, 6.0F, 3.0F),
                PartPose.offsetAndRotation(0.0F, -1.0F, -5.0F, 0.4F, 0.0F, 0.0F));

        PartDefinition head = neck.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 19)
                        .addBox(-2.5F, -4.0F, -4.0F, 5.0F, 5.0F, 5.0F),
                PartPose.offset(0.0F, -5.0F, 0.0F));

        // Head Details
        head.addOrReplaceChild("ear_left", CubeListBuilder.create().texOffs(36, 27).addBox(0.0F, -1.0F, -0.5F, 2.0F, 2.0F, 1.0F), PartPose.offset(2.0F, -3.5F, -1.0F));
        head.addOrReplaceChild("ear_right", CubeListBuilder.create().texOffs(36, 30).addBox(-2.0F, -1.0F, -0.5F, 2.0F, 2.0F, 1.0F), PartPose.offset(-2.0F, -3.5F, -1.0F));
        head.addOrReplaceChild("nose", CubeListBuilder.create().texOffs(34, 19).addBox(-1.5F, -1.0F, -1.0F, 3.0F, 2.0F, 2.0F), PartPose.offset(0.0F, -1.0F, -4.0F));

        // 3. LEGS (Attached to Body)
        // Note the addBox Y is 0.0F so they rotate exactly at the body joint
        CubeListBuilder legBuilder = CubeListBuilder.create().texOffs(20, 28).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F);

        body.addOrReplaceChild("leg_back_left", legBuilder, PartPose.offset(2.0F, 2.0F, 5.0F));
        body.addOrReplaceChild("leg_back_right", CubeListBuilder.create().texOffs(28, 28).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F), PartPose.offset(-2.0F, 2.0F, 5.0F));
        body.addOrReplaceChild("leg_front_left", CubeListBuilder.create().texOffs(0, 29).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F), PartPose.offset(2.0F, 2.0F, -4.0F));
        body.addOrReplaceChild("leg_front_right", CubeListBuilder.create().texOffs(8, 29).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F), PartPose.offset(-2.0F, 2.0F, -4.0F));

        // 4. TAIL
        body.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(34, 23).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 3.0F, 1.0F), PartPose.offset(0.0F, -2.0F, 7.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        // RESET (Crucial for stability)
        this.head.yRot = netHeadYaw * ((float)Math.PI / 180F);
        this.head.xRot = headPitch * ((float)Math.PI / 180F);

        // STABLE MOVEMENT
        // Limiting degree to 0.6F prevents legs from clipping through the body
        float speed = 0.6662F;
        float degree = 0.6F;

        this.leg_back_left.xRot = Mth.cos(limbSwing * speed) * degree * limbSwingAmount;
        this.leg_back_right.xRot = Mth.cos(limbSwing * speed + (float)Math.PI) * degree * limbSwingAmount;
        this.leg_front_left.xRot = Mth.cos(limbSwing * speed + (float)Math.PI) * degree * limbSwingAmount;
        this.leg_front_right.xRot = Mth.cos(limbSwing * speed) * degree * limbSwingAmount;

        // IDLE (Subtle and non-intrusive)
        this.tail.xRot = 0.5F + Mth.cos(ageInTicks * 0.1F) * 0.1F;

        // Gentle "breathing" on the neck instead of ears
        this.body.getChild("neck").xRot = 0.4F + Mth.sin(ageInTicks * 0.05F) * 0.02F;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
        this.root.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
    }
}