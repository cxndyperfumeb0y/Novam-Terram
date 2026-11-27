package net.candyperfumeboy.novamterram.entities.custom;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

/**
 * Faun model for Novam Terram. Walks, bounds when fleeing, grazes, freezes when
 * watched, spars, lies down to sleep, and wears a flower crown after eating flowers.
 * Model space: y points down (ground = 24), the faun faces -z.
 */
public class FaunModel<T extends FaunEntity> extends EntityModel<T> {

    private final ModelPart root;
    private final ModelPart body;
    private final ModelPart neck;
    private final ModelPart head;
    private final ModelPart earLeft;
    private final ModelPart earRight;
    private final ModelPart antlers;
    private final ModelPart crown;
    private final ModelPart tail;
    private final ModelPart legFr;
    private final ModelPart legFl;
    private final ModelPart legBr;
    private final ModelPart legBl;

    private float grazeAmount;

    public FaunModel(ModelPart modelRoot) {
        this.root = modelRoot.getChild("root");
        this.body = this.root.getChild("body");
        this.tail = this.body.getChild("tail");
        this.neck = this.body.getChild("neck");
        this.head = this.neck.getChild("head");
        this.earLeft = this.head.getChild("ear_left");
        this.earRight = this.head.getChild("ear_right");
        this.antlers = this.head.getChild("antlers");
        this.crown = this.head.getChild("crown");
        this.legFr = this.root.getChild("leg_fr");
        this.legFl = this.root.getChild("leg_fl");
        this.legBr = this.root.getChild("leg_br");
        this.legBl = this.root.getChild("leg_bl");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();

        PartDefinition root = mesh.getRoot().addOrReplaceChild("root",
                CubeListBuilder.create(),
                PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition body = root.addOrReplaceChild("body",
                CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-4.0F, -7.0F, -7.0F, 8.0F, 9.0F, 14.0F),
                PartPose.offset(0.0F, -14.0F, 0.0F));

        body.addOrReplaceChild("tail",
                CubeListBuilder.create()
                        .texOffs(30, 0).addBox(-1.5F, 0.0F, -1.0F, 3.0F, 5.0F, 2.0F),
                PartPose.offsetAndRotation(0.0F, -6.0F, 7.0F, 0.5F, 0.0F, 0.0F));

        PartDefinition neck = body.addOrReplaceChild("neck",
                CubeListBuilder.create()
                        .texOffs(0, 23).addBox(-2.0F, -9.0F, -2.0F, 4.0F, 11.0F, 5.0F),
                PartPose.offsetAndRotation(0.0F, -5.0F, -6.0F, 0.3491F, 0.0F, 0.0F));

        PartDefinition head = neck.addOrReplaceChild("head",
                CubeListBuilder.create()
                        .texOffs(18, 23).addBox(-3.0F, -6.0F, -3.0F, 6.0F, 6.0F, 7.0F)
                        .texOffs(30, 7).addBox(-2.0F, -3.0F, -6.0F, 4.0F, 3.0F, 3.0F),
                PartPose.offsetAndRotation(0.0F, -8.0F, -1.0F, -0.3491F, 0.0F, 0.0F));

        head.addOrReplaceChild("ear_left",
                CubeListBuilder.create()
                        .texOffs(0, 10).addBox(0.0F, -2.0F, 0.0F, 4.0F, 3.0F, 1.0F),
                PartPose.offsetAndRotation(2.8F, -4.0F, 1.0F, 0.0F, 0.0F, -0.45F));

        head.addOrReplaceChild("ear_right",
                CubeListBuilder.create()
                        .texOffs(13, 23).addBox(-4.0F, -2.0F, 0.0F, 4.0F, 3.0F, 1.0F),
                PartPose.offsetAndRotation(-2.8F, -4.0F, 1.0F, 0.0F, 0.0F, 0.45F));

        PartDefinition antlers = head.addOrReplaceChild("antlers",
                CubeListBuilder.create(),
                PartPose.offset(0.0F, -6.0F, 0.0F));

        antlers.addOrReplaceChild("antler_left",
                CubeListBuilder.create()
                        .texOffs(40, 0).addBox(-0.5F, -6.0F, -0.5F, 1.0F, 6.0F, 1.0F)
                        .texOffs(54, 16).addBox(-0.5F, -2.0F, -2.5F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.001F))
                        .texOffs(58, 16).addBox(0.5F, -4.0F, -0.5F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.002F))
                        .texOffs(10, 0).addBox(1.5F, -6.0F, -0.5F, 1.0F, 2.0F, 1.0F)
                        .texOffs(0, 0).addBox(-0.5F, -8.0F, 0.5F, 1.0F, 3.0F, 1.0F, new CubeDeformation(-0.01F)),
                PartPose.offsetAndRotation(1.8F, 0.0F, 0.0F, -0.15F, 0.0F, 0.35F));

        antlers.addOrReplaceChild("antler_right",
                CubeListBuilder.create()
                        .texOffs(40, 0).addBox(-0.5F, -6.0F, -0.5F, 1.0F, 6.0F, 1.0F)
                        .texOffs(54, 16).addBox(-0.5F, -2.0F, -2.5F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.001F))
                        .texOffs(58, 16).addBox(-2.5F, -4.0F, -0.5F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.002F))
                        .texOffs(10, 0).addBox(-2.5F, -6.0F, -0.5F, 1.0F, 2.0F, 1.0F)
                        .texOffs(0, 0).addBox(-0.5F, -8.0F, 0.5F, 1.0F, 3.0F, 1.0F, new CubeDeformation(-0.01F)),
                PartPose.offsetAndRotation(-1.8F, 0.0F, 0.0F, -0.15F, 0.0F, -0.35F));

        head.addOrReplaceChild("crown",
                CubeListBuilder.create()
                        .texOffs(44, 34).addBox(-3.0F, -1.0F, -3.4F, 6.0F, 1.0F, 1.0F, new CubeDeformation(0.05F))
                        .texOffs(56, 0).addBox(-3.2F, -1.8F, -3.7F, 2.0F, 2.0F, 2.0F)
                        .texOffs(56, 4).addBox(1.2F, -1.4F, -3.6F, 2.0F, 2.0F, 2.0F, new CubeDeformation(-0.2F))
                        .texOffs(56, 8).addBox(2.0F, -1.2F, -0.5F, 2.0F, 2.0F, 2.0F, new CubeDeformation(-0.25F))
                        .texOffs(56, 12).addBox(-4.0F, -1.2F, 0.5F, 2.0F, 2.0F, 2.0F, new CubeDeformation(-0.25F)),
                PartPose.offset(0.0F, -6.0F, 0.0F));

        root.addOrReplaceChild("leg_fr",
                CubeListBuilder.create()
                        .texOffs(44, 0).addBox(-1.5F, 0.0F, -1.5F, 3.0F, 14.0F, 3.0F, new CubeDeformation(-0.02F)),
                PartPose.offset(2.5F, -14.0F, -5.0F));

        root.addOrReplaceChild("leg_fl",
                CubeListBuilder.create()
                        .texOffs(44, 0).addBox(-1.5F, 0.0F, -1.5F, 3.0F, 14.0F, 3.0F, new CubeDeformation(-0.02F)),
                PartPose.offset(-2.5F, -14.0F, -5.0F));

        root.addOrReplaceChild("leg_br",
                CubeListBuilder.create()
                        .texOffs(44, 17).addBox(-1.5F, 0.0F, -1.5F, 3.0F, 14.0F, 3.0F, new CubeDeformation(-0.02F))
                        .texOffs(0, 0).addBox(-1.5F, -2.0F, -2.0F, 3.0F, 6.0F, 4.0F, new CubeDeformation(0.15F)),
                PartPose.offset(2.5F, -14.0F, 5.0F));

        root.addOrReplaceChild("leg_bl",
                CubeListBuilder.create()
                        .texOffs(44, 17).addBox(-1.5F, 0.0F, -1.5F, 3.0F, 14.0F, 3.0F, new CubeDeformation(-0.02F))
                        .texOffs(0, 0).addBox(-1.5F, -2.0F, -2.0F, 3.0F, 6.0F, 4.0F, new CubeDeformation(0.15F)),
                PartPose.offset(-2.5F, -14.0F, 5.0F));

        return LayerDefinition.create(mesh, 64, 64);
    }

    // Runs before setupAnim and has the partial tick, which keeps grazing smooth
    @Override
    public void prepareMobModel(T entity, float limbSwing, float limbSwingAmount, float partialTick) {
        this.grazeAmount = entity.getGrazeAmount(partialTick);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root.getAllParts().forEach(ModelPart::resetPose);

        this.antlers.visible = !entity.isBaby() && entity.isBuck() && entity.hasAntlers();
        this.crown.visible = entity.isFloral();

        boolean hiding = entity.isHiding();
        // Offset per faun so a herd doesn't twitch in sync
        float time = ageInTicks + entity.getId() * 23;
        float graze = this.grazeAmount;

        // Looking around (added on top of the resting pose)
        float look = (hiding ? 0.3F : 1.0F) * (1.0F - graze);
        this.head.yRot += netHeadYaw * Mth.DEG_TO_RAD * look;
        this.head.xRot += headPitch * Mth.DEG_TO_RAD * look;

        // Walking, turning into a bounding gallop when running
        float amount = Math.min(limbSwingAmount, 1.0F) * (hiding ? 0.05F : 1.0F);
        float run = Mth.clamp((amount - 0.7F) * 4.0F, 0.0F, 1.0F);
        float step = limbSwing * 0.6662F;

        float walkA = Mth.cos(step) * 1.2F * amount;
        float front = Mth.cos(step) * 0.9F;
        float back = Mth.cos(step + Mth.PI) * 0.9F;

        this.legFr.xRot = Mth.lerp(run, walkA, front);
        this.legFl.xRot = Mth.lerp(run, -walkA, front + 0.15F);
        this.legBr.xRot = Mth.lerp(run, -walkA, back);
        this.legBl.xRot = Mth.lerp(run, walkA, back - 0.15F);

        this.root.y -= run * Mth.abs(Mth.sin(step)) * 2.5F;
        this.body.xRot = run * Mth.sin(step) * 0.08F;

        // White tail goes up when fleeing, and flicks now and then
        this.tail.xRot += run * 1.9F;
        if (!hiding && Mth.sin(time * 0.05F) > 0.9F) {
            this.tail.zRot = Mth.sin(time * 1.2F) * 0.35F;
        }

        if (hiding) {
            // Frozen: head high, ears pricked forward
            this.neck.xRot -= 0.2F;
            this.head.xRot += 0.2F;
            this.earLeft.zRot -= 0.35F;
            this.earRight.zRot += 0.35F;
            this.earLeft.yRot = 0.25F;
            this.earRight.yRot = -0.25F;
        } else {
            // Ear twitch
            if (Mth.sin(time * 0.07F) > 0.95F) {
                this.earLeft.zRot += Mth.sin(time * 1.5F) * 0.25F;
            }
            if (Mth.sin(time * 0.06F + 2.0F) > 0.95F) {
                this.earRight.zRot += Mth.sin(time * 1.5F) * 0.25F;
            }
        }

        // Bucks with antlers lowered, shoving back and forth
        if (entity.isSparring()) {
            float shove = Mth.sin(ageInTicks * 0.8F);
            this.neck.xRot = 1.5F + shove * 0.1F;
            this.head.xRot = -0.55F;
            this.head.yRot = shove * 0.2F;
            this.root.z = -shove;
        }

        // Lying down with legs folded
        if (entity.isResting()) {
            this.root.y = 35.0F;
            for (ModelPart leg : new ModelPart[]{this.legFr, this.legFl, this.legBr, this.legBl}) {
                leg.xRot = -Mth.HALF_PI;
                leg.yScale = 0.55F;
            }
            this.body.y += Mth.sin(time * 0.08F) * 0.25F; // slow breathing
            this.neck.xRot = 0.5F;
            this.head.xRot = -0.35F;
            this.head.yRot = 0.0F;
        }

        // Head down to graze, with a small chewing motion
        if (graze > 0.0F) {
            this.body.xRot += 0.18F * graze;
            this.neck.xRot = Mth.lerp(graze, this.neck.xRot, 2.6F);
            this.neck.z -= graze;
            this.head.xRot = Mth.lerp(graze, this.head.xRot, -1.3F + Mth.sin(ageInTicks * 0.9F) * 0.08F);
        }

        // Fawns get a bigger head
        float headSize = this.young ? 1.3F : 1.0F;
        this.head.xScale = headSize;
        this.head.yScale = headSize;
        this.head.zScale = headSize;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
        poseStack.pushPose();
        if (this.young) {
            // 55% size, pushed down so the hooves stay on the ground
            poseStack.scale(0.55F, 0.55F, 0.55F);
            poseStack.translate(0.0D, 1.227D, 0.0D);
        }
        this.root.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        poseStack.popPose();
    }
}
