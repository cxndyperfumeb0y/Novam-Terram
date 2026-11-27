package net.candyperfumeboy.novamterram.entities.custom;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.Parrot;

/**
 * Songbird Model for Novam Terram.
 * Handles ground hopping, flight, sitting, and perching on Iron Golems.
 * Model space: y points down (ground = 24), the bird faces -z.
 */
public class SongbirdModel<T extends Entity> extends HierarchicalModel<T> {

    private final ModelPart root;
    private final ModelPart body;
    private final ModelPart head;
    private final ModelPart wingLeft;
    private final ModelPart wingRight;
    private final ModelPart tail;
    private final ModelPart legLeft;
    private final ModelPart legRight;

    public SongbirdModel(ModelPart modelRoot) {
        this.root = modelRoot.getChild("root");
        this.body = this.root.getChild("body");
        this.head = this.body.getChild("head");
        this.wingLeft = this.body.getChild("wing_left");
        this.wingRight = this.body.getChild("wing_right");
        this.tail = this.body.getChild("tail");
        this.legLeft = this.root.getChild("leg_left");
        this.legRight = this.root.getChild("leg_right");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();

        PartDefinition root = mesh.getRoot().addOrReplaceChild("root",
                CubeListBuilder.create(),
                PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition body = root.addOrReplaceChild("body",
                CubeListBuilder.create(),
                PartPose.offset(0.0F, -2.0F, 0.0F));

        body.addOrReplaceChild("body_r1",
                CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-1.5F, -1.5F, -2.0F, 3.0F, 3.0F, 4.0F),
                PartPose.offsetAndRotation(0.0F, -1.5F, 0.0F, -0.26F, 0.0F, 0.0F));

        body.addOrReplaceChild("head",
                CubeListBuilder.create()
                        .texOffs(0, 7).addBox(-1.5F, -3.0F, -1.8F, 3.0F, 3.0F, 3.0F)
                        .texOffs(14, 0).addBox(-0.5F, -2.0F, -2.8F, 1.0F, 1.0F, 1.0F),
                PartPose.offset(0.0F, -2.4F, -1.8F));

        body.addOrReplaceChild("wing_left",
                CubeListBuilder.create()
                        .texOffs(14, 0).addBox(-1.0F, 0.0F, -1.5F, 1.0F, 3.0F, 4.0F)
                        .texOffs(20, 0).addBox(-1.0F, 1.0F, 2.5F, 1.0F, 1.0F, 1.0F),
                PartPose.offsetAndRotation(-1.5F, -3.2F, 0.0F, 0.0F, 0.0F, 0.03F));

        body.addOrReplaceChild("wing_right",
                CubeListBuilder.create()
                        .texOffs(20, 3).addBox(0.0F, 0.0F, -1.5F, 1.0F, 3.0F, 4.0F)
                        .texOffs(24, 0).addBox(0.0F, 1.0F, 2.5F, 1.0F, 1.0F, 1.0F),
                PartPose.offsetAndRotation(1.5F, -3.2F, 0.0F, 0.0F, 0.0F, -0.03F));

        body.addOrReplaceChild("tail",
                CubeListBuilder.create()
                        .texOffs(12, 7).addBox(-1.0F, -0.5F, 0.0F, 2.0F, 1.0F, 4.0F),
                PartPose.offsetAndRotation(0.0F, -1.4F, 1.8F, 0.2F, 0.0F, 0.0F));

        root.addOrReplaceChild("leg_left",
                CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-0.5F, 0.0F, -0.5F, 1.0F, 2.0F, 1.0F)
                        .texOffs(12, 0).addBox(-0.5F, 1.98F, -1.5F, 1.0F, 0.0F, 1.0F),
                PartPose.offset(-0.8F, -2.0F, 0.0F));

        root.addOrReplaceChild("leg_right",
                CubeListBuilder.create()
                        .texOffs(10, 0).addBox(-0.5F, 0.0F, -0.5F, 1.0F, 2.0F, 1.0F)
                        .texOffs(22, 0).addBox(-0.5F, 1.98F, -1.5F, 1.0F, 0.0F, 1.0F),
                PartPose.offset(0.8F, -2.0F, 0.0F));

        return LayerDefinition.create(mesh, 32, 32);
    }

    @Override
    public ModelPart root() {
        return this.root;
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root.getAllParts().forEach(ModelPart::resetPose);

        // Looking around
        this.head.yRot = netHeadYaw * Mth.DEG_TO_RAD;
        this.head.xRot = headPitch * Mth.DEG_TO_RAD;

        boolean sitting = entity instanceof TamableAnimal pet && pet.isInSittingPose();
        boolean asleep = entity instanceof SongbirdEntity songbird && songbird.isRoosting();
        boolean onHead = entity instanceof SongbirdEntity perched && perched.isOnHead();
        boolean settled = sitting || asleep || onHead;
        boolean flying = !settled && !entity.onGround() && !entity.isPassenger();
        // Offset per bird so a flock doesn't move in sync
        float time = ageInTicks + entity.getId() * 17;

        if (flying) {
            // Lean forward, tuck the legs, flap outward only so the wings never cut into the body
            float flap = (Mth.sin(ageInTicks * 1.3F) + 1.0F) * 0.8F;
            this.wingLeft.zRot = flap;
            this.wingRight.zRot = -flap;
            this.body.xRot = 0.35F;
            this.head.xRot -= 0.35F;
            this.tail.xRot = Mth.sin(time * 0.3F) * 0.1F;
            this.legLeft.xRot = 0.9F;
            this.legRight.xRot = 0.9F;
            this.root.y = 22.5F + Mth.sin(time * 0.2F) * 0.5F;
        } else {
            // Gentle breathing and tail flicks
            float breath = 0.03F + Mth.sin(time * 0.15F) * 0.03F;
            this.wingLeft.zRot = breath;
            this.wingRight.zRot = -breath;
            this.tail.xRot += Mth.cos(time * 0.2F) * 0.1F;

            // Curious head tilt now and then
            if (Mth.sin(time * 0.1F) > 0.8F) {
                this.head.zRot = Mth.cos(time * 0.8F) * 0.2F;
            }

            // Hopping (not while riding something)
            if (!settled && !entity.isPassenger() && limbSwingAmount > 0.1F) {
                float hop = Mth.abs(Mth.cos(limbSwing * 2.5F)) * Math.min(limbSwingAmount, 1.0F);
                this.root.y -= hop * 1.5F;
                this.body.xRot = 0.25F;
            }
        }

        // Dance near a playing jukebox
        if (!flying && !settled && entity instanceof Parrot parrot && parrot.isPartyParrot()) {
            float beat = Mth.sin(time * 0.5F);
            this.root.y -= Mth.abs(beat) * 1.2F;
            this.body.zRot = beat * 0.2F;
            this.head.zRot = -beat * 0.35F;
            this.tail.yRot = beat * 0.4F;
            this.wingLeft.zRot = Mth.abs(beat) * 0.8F;
            this.wingRight.zRot = -Mth.abs(beat) * 0.8F;
        }

        // Head up while singing
        if (entity instanceof SongbirdEntity bird && bird.getSingTicks() > 0) {
            this.head.xRot -= 0.5F + Mth.sin(ageInTicks * 2.0F) * 0.15F;
        }

        if (settled) {
            // Settle down onto the belly
            this.root.y = 25.4F;
        }
        this.legLeft.visible = !settled;
        this.legRight.visible = !settled;

        if (asleep) {
            // Head turned back and tucked down, with slow breathing
            this.head.yRot = 2.6F;
            this.head.xRot = 0.25F;
            this.head.zRot = 0.0F;
            this.head.y += 0.6F + Mth.sin(time * 0.08F) * 0.15F;
            this.head.z += 1.2F; // back over the shoulders
        }

        // Fledglings get a slightly bigger head
        float headSize = this.young ? 1.2F : 1.0F;
        this.head.xScale = headSize;
        this.head.yScale = headSize;
        this.head.zScale = headSize;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
        poseStack.pushPose();
        if (this.young) {
            // 70% size, pushed down so the feet stay on the ground
            poseStack.scale(0.7F, 0.7F, 0.7F);
            poseStack.translate(0.0D, 0.643D, 0.0D);
        }
        super.renderToBuffer(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        poseStack.popPose();
    }
}
