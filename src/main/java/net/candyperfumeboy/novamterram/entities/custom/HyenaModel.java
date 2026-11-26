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

/**
 * Hyena: a chunky little body, stubby legs, a big round head with big ears and eyes,
 * and a fluffy tail.
 * Model space: y points down (ground = 24), the head points toward -z.
 */
public class HyenaModel<T extends HyenaEntity> extends HierarchicalModel<T> {

    private final ModelPart root;
    private final ModelPart body;
    private final ModelPart chest;
    private final ModelPart chestMane;
    private final ModelPart neck;
    private final ModelPart neckMane;
    private final ModelPart head;
    private final ModelPart jaw;
    private final ModelPart rightEar;
    private final ModelPart leftEar;
    private final ModelPart tail;
    private final ModelPart rightFrontLeg;
    private final ModelPart leftFrontLeg;
    private final ModelPart rightHindLeg;
    private final ModelPart leftHindLeg;

    public HyenaModel(ModelPart root) {
        this.root = root;
        this.body = root.getChild("body");
        this.chest = this.body.getChild("chest");
        this.chestMane = this.chest.getChild("chest_mane");
        this.neck = this.body.getChild("neck");
        this.neckMane = this.neck.getChild("neck_mane");
        this.head = this.neck.getChild("head");
        this.jaw = this.head.getChild("jaw");
        this.rightEar = this.head.getChild("right_ear");
        this.leftEar = this.head.getChild("left_ear");
        this.tail = this.body.getChild("tail");
        this.rightFrontLeg = root.getChild("right_front_leg");
        this.leftFrontLeg = root.getChild("left_front_leg");
        this.rightHindLeg = root.getChild("right_hind_leg");
        this.leftHindLeg = root.getChild("left_hind_leg");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        PartDefinition body = root.addOrReplaceChild("body",
                CubeListBuilder.create()
                        .texOffs(0, 17).addBox(-3.5F, -3.0F, -5.0F, 7.0F, 7.0F, 9.0F),
                PartPose.offset(0.0F, 13.0F, 6.0F));

        PartDefinition chest = body.addOrReplaceChild("chest",
                CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 9.0F),
                PartPose.offset(0.0F, 0.2F, -7.0F));

        chest.addOrReplaceChild("chest_mane",
                CubeListBuilder.create()
                        .texOffs(42, 33).addBox(-1.0F, -1.0F, -4.0F, 2.0F, 1.0F, 9.0F),
                PartPose.offset(0.0F, -3.8F, 0.0F));

        PartDefinition neck = body.addOrReplaceChild("neck",
                CubeListBuilder.create()
                        .texOffs(22, 33).addBox(-2.5F, -3.0F, -4.0F, 5.0F, 6.0F, 5.0F),
                PartPose.offsetAndRotation(0.0F, -1.0F, -11.0F, -0.4F, 0.0F, 0.0F));

        neck.addOrReplaceChild("neck_mane",
                CubeListBuilder.create()
                        .texOffs(52, 44).addBox(-1.0F, -1.0F, -3.0F, 2.0F, 1.0F, 4.0F),
                PartPose.offset(0.0F, -2.8F, 0.0F));

        PartDefinition head = neck.addOrReplaceChild("head",
                CubeListBuilder.create()
                        .texOffs(32, 17).addBox(-4.0F, -3.5F, -4.0F, 8.0F, 7.0F, 6.0F)
                        .texOffs(0, 53).addBox(-2.0F, 0.0F, -8.0F, 4.0F, 3.0F, 4.0F),
                PartPose.offsetAndRotation(0.0F, -1.0F, -4.0F, 0.4F, 0.0F, 0.0F));

        head.addOrReplaceChild("jaw",
                CubeListBuilder.create()
                        .texOffs(16, 53).addBox(-1.5F, 0.0F, -3.5F, 3.0F, 1.0F, 4.0F),
                PartPose.offset(0.0F, 2.9F, -4.0F));

        head.addOrReplaceChild("right_ear",
                CubeListBuilder.create()
                        .texOffs(30, 53).addBox(-2.0F, -3.0F, -0.5F, 4.0F, 3.0F, 1.0F)
                        .texOffs(58, 0).addBox(-1.0F, -4.0F, -0.5F, 2.0F, 1.0F, 1.0F),
                PartPose.offsetAndRotation(-3.0F, -3.3F, -1.0F, 0.0F, 0.0F, -0.3F));

        head.addOrReplaceChild("left_ear",
                CubeListBuilder.create()
                        .texOffs(40, 53).addBox(-2.0F, -3.0F, -0.5F, 4.0F, 3.0F, 1.0F)
                        .texOffs(50, 53).addBox(-1.0F, -4.0F, -0.5F, 2.0F, 1.0F, 1.0F),
                PartPose.offsetAndRotation(3.0F, -3.3F, -1.0F, 0.0F, 0.0F, 0.3F));

        body.addOrReplaceChild("tail",
                CubeListBuilder.create()
                        .texOffs(0, 33).addBox(-1.5F, -1.5F, 0.0F, 3.0F, 3.0F, 8.0F),
                PartPose.offsetAndRotation(0.0F, -1.0F, 3.5F, -0.8F, 0.0F, 0.0F));

        root.addOrReplaceChild("right_front_leg",
                CubeListBuilder.create()
                        .texOffs(34, 0).addBox(-1.5F, 0.0F, -1.5F, 3.0F, 12.0F, 3.0F),
                PartPose.offset(-2.7F, 12.0F, -3.0F));

        root.addOrReplaceChild("left_front_leg",
                CubeListBuilder.create()
                        .texOffs(46, 0).addBox(-1.5F, 0.0F, -1.5F, 3.0F, 12.0F, 3.0F),
                PartPose.offset(2.7F, 12.0F, -3.0F));

        root.addOrReplaceChild("right_hind_leg",
                CubeListBuilder.create()
                        .texOffs(0, 44).addBox(-1.5F, -1.0F, -2.0F, 3.0F, 5.0F, 4.0F)
                        .texOffs(28, 44).addBox(-1.5F, 4.0F, -1.5F, 3.0F, 6.0F, 3.0F),
                PartPose.offset(-2.6F, 14.0F, 7.0F));

        root.addOrReplaceChild("left_hind_leg",
                CubeListBuilder.create()
                        .texOffs(14, 44).addBox(-1.5F, -1.0F, -2.0F, 3.0F, 5.0F, 4.0F)
                        .texOffs(40, 44).addBox(-1.5F, 4.0F, -1.5F, 3.0F, 6.0F, 3.0F),
                PartPose.offset(2.6F, 14.0F, 7.0F));

        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public ModelPart root() {
        return this.root;
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root.getAllParts().forEach(ModelPart::resetPose);

        boolean sitting = entity.isInSittingPose();
        boolean resting = !sitting && entity.isResting();
        boolean stalking = !sitting && !resting && entity.isStalking();
        boolean attacking = !sitting && !resting && !stalking && entity.isAggressive();
        boolean frenzied = entity.isFrenzied() && !entity.isTame();

        // Looking around: mostly the head, a little from the neck
        this.neck.yRot = netHeadYaw * Mth.DEG_TO_RAD * 0.2F;
        this.head.yRot = netHeadYaw * Mth.DEG_TO_RAD * 0.5F;
        this.head.xRot += headPitch * Mth.DEG_TO_RAD * 0.5F;

        // Gentle tail swish; tamed hyenas wag happily
        this.tail.yRot = entity.isTame()
                ? Mth.sin(ageInTicks * 0.5F) * 0.35F
                : Mth.sin(ageInTicks * 0.08F) * 0.15F;

        // The lower jaw only shows while attacking or panting
        this.jaw.visible = false;

        if (sitting) {
            applySittingPose();
        } else if (resting) {
            applyRestingPose(ageInTicks);
        } else {
            float swing = Mth.cos(limbSwing * 0.6662F) * 1.2F * limbSwingAmount;
            this.rightFrontLeg.xRot = swing;
            this.leftHindLeg.xRot = swing;
            this.leftFrontLeg.xRot = -swing;
            this.rightHindLeg.xRot = -swing;
        }

        if (stalking) {
            // Body low, head down and level
            this.body.y = 15.0F;
            this.neck.xRot += 0.5F;
            this.head.xRot -= 0.45F;
            this.tail.xRot = 0.1F;
        }

        // Quick ear twitch every few seconds (offset per hyena so they don't sync)
        float twitch = (ageInTicks + entity.getId() * 37) % 100.0F;
        if (twitch >= 0.0F && twitch < 6.0F) {
            this.rightEar.zRot -= Mth.sin(twitch / 6.0F * Mth.PI) * 0.3F;
        }

        float maneHeight = 1.0F;
        if (attacking) {
            // Head low, mouth open, mane up
            this.neck.xRot += 0.4F;
            this.head.xRot -= 0.3F;
            this.jaw.visible = true;
            this.jaw.xRot = 0.4F;
            this.tail.xRot = 0.0F;
            maneHeight = 1.8F;
        } else if (frenzied && !sitting && !resting) {
            this.jaw.visible = true;
            this.jaw.xRot = 0.25F + Mth.sin(ageInTicks * 0.5F) * 0.08F;
            maneHeight = 1.4F;
        }
        if (entity.getLaughTicks() > 0) {
            // Head thrown back, bobbing, jaw going
            float bob = Mth.sin(ageInTicks * 1.8F);
            this.head.xRot -= 0.35F + bob * 0.2F;
            this.jaw.visible = true;
            this.jaw.xRot = 0.45F + bob * 0.15F;
        }
        this.chestMane.yScale = maneHeight;
        this.neckMane.yScale = maneHeight;

        // Pups get a slightly bigger head
        float headSize = this.young ? 1.2F : 1.0F;
        this.head.xScale = headSize;
        this.head.yScale = headSize;
        this.head.zScale = headSize;
    }

    private void applySittingPose() {
        // Rear on the ground, chest up
        this.body.y = 18.5F;
        this.body.xRot = -0.5F;

        // Keep the face level
        this.neck.xRot += 0.25F;
        this.head.xRot += 0.25F;
        this.tail.xRot = 0.2F;

        // Hind legs folded forward along the ground
        this.rightHindLeg.setPos(-4.2F, 22.0F, 7.0F);
        this.leftHindLeg.setPos(4.2F, 22.0F, 7.0F);
        this.rightHindLeg.xRot = -Mth.HALF_PI;
        this.leftHindLeg.xRot = -Mth.HALF_PI;
        this.rightHindLeg.yRot = 0.15F;
        this.leftHindLeg.yRot = -0.15F;
    }

    private void applyRestingPose(float ageInTicks) {
        // Lying flat on the belly, legs stretched out, slow breathing
        this.body.y = 19.8F;
        this.neck.xRot += 0.35F;
        this.neck.y += Mth.sin(ageInTicks * 0.05F) * 0.25F;
        this.head.xRot -= 0.35F;
        this.head.yRot *= 0.3F;
        this.neck.yRot *= 0.3F;
        this.tail.xRot = -0.2F;
        this.tail.yRot = 0.4F;

        this.rightFrontLeg.setPos(-3.6F, 22.4F, -3.0F);
        this.leftFrontLeg.setPos(3.6F, 22.4F, -3.0F);
        this.rightHindLeg.setPos(-4.2F, 22.0F, 7.0F);
        this.leftHindLeg.setPos(4.2F, 22.0F, 7.0F);
        this.rightFrontLeg.xRot = -Mth.HALF_PI;
        this.leftFrontLeg.xRot = -Mth.HALF_PI;
        this.rightHindLeg.xRot = -Mth.HALF_PI;
        this.leftHindLeg.xRot = -Mth.HALF_PI;
        this.rightHindLeg.yRot = 0.15F;
        this.leftHindLeg.yRot = -0.15F;
    }

    /** Moves the pose stack to the head, for drawing things held in the mouth. */
    public void translateToHead(PoseStack poseStack) {
        applyScale(poseStack);
        this.body.translateAndRotate(poseStack);
        this.neck.translateAndRotate(poseStack);
        this.head.translateAndRotate(poseStack);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
        poseStack.pushPose();
        applyScale(poseStack);
        super.renderToBuffer(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        poseStack.popPose();
    }

    // Shrink toward the ground so the paws stay on it
    private void applyScale(PoseStack poseStack) {
        if (this.young) {
            poseStack.scale(0.45F, 0.45F, 0.45F);
            poseStack.translate(0.0D, 1.833D, 0.0D);
        } else {
            poseStack.scale(0.85F, 0.85F, 0.85F);
            poseStack.translate(0.0D, 0.265D, 0.0D);
        }
    }
}
