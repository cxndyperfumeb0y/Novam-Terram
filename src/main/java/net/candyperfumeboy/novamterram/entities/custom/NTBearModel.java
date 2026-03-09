package net.candyperfumeboy.novamterram.entities.custom;

import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class NTBearModel<T extends AbstractNTBear> extends HierarchicalModel<T> {
    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart body;
    private final ModelPart rightHindLeg;
    private final ModelPart leftHindLeg;
    private final ModelPart rightFrontLeg;
    private final ModelPart leftFrontLeg;

    public NTBearModel(ModelPart root) {
        this.root = root;
        this.head = root.getChild("head");
        this.body = root.getChild("body");
        this.rightHindLeg = root.getChild("right_hind_leg");
        this.leftHindLeg = root.getChild("left_hind_leg");
        this.rightFrontLeg = root.getChild("right_front_leg");
        this.leftFrontLeg = root.getChild("left_front_leg");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        root.addOrReplaceChild("head", CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-3.5F, -3.0F, -3.0F, 7.0F, 7.0F, 7.0F)
                        .texOffs(0, 44).addBox("mouth", -2.5F, 1.0F, -6.0F, 5.0F, 3.0F, 3.0F)
                        .texOffs(26, 0).addBox("right_ear", -4.5F, -4.0F, -1.0F, 2.0F, 2.0F, 1.0F)
                        .texOffs(26, 0).mirror().addBox("left_ear", 2.5F, -4.0F, -1.0F, 2.0F, 2.0F, 1.0F),
                PartPose.offset(0.0F, 10.0F, -16.0F));

        root.addOrReplaceChild("body", CubeListBuilder.create()
                        .texOffs(0, 19).addBox(-5.0F, -13.0F, -7.0F, 14.0F, 14.0F, 11.0F)
                        .texOffs(39, 0).addBox(-4.0F, -25.0F, -7.0F, 12.0F, 12.0F, 10.0F),
                // Add the rotation directly to the PartPose here
                PartPose.offsetAndRotation(-2.0F, 9.0F, 12.0F, (float)(Math.PI / 2), 0.0F, 0.0F));

        CubeListBuilder hindLegs = CubeListBuilder.create().texOffs(50, 22).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 10.0F, 8.0F);
        root.addOrReplaceChild("right_hind_leg", hindLegs, PartPose.offset(-4.5F, 14.0F, 6.0F));
        root.addOrReplaceChild("left_hind_leg", hindLegs, PartPose.offset(4.5F, 14.0F, 6.0F));

        CubeListBuilder frontLegs = CubeListBuilder.create().texOffs(50, 40).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 10.0F, 6.0F);
        root.addOrReplaceChild("right_front_leg", frontLegs, PartPose.offset(-3.5F, 14.0F, -8.0F));
        root.addOrReplaceChild("left_front_leg", frontLegs, PartPose.offset(3.5F, 14.0F, -8.0F));

        return LayerDefinition.create(mesh, 128, 64);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        // 1. Reset parts to bind pose
        this.root().getAllParts().forEach(ModelPart::resetPose);

        // 2. Set default "Quadruped" body orientation (usually PI/2 for Minecraft animals)
        this.body.xRot = (float) (Math.PI / 2);

        // 3. Head tracking
        this.head.xRot = headPitch * ((float)Math.PI / 180F);
        this.head.yRot = netHeadYaw * ((float)Math.PI / 180F);

        // 4. Standing logic (relative to the default body pose)
        if (entity.isStanding()) {
            float scale = entity.getStandingAnimationScale(0.0F);
            // Subtract from the PI/2 default instead of setting from 0
            this.body.xRot = (float) (Math.PI / 2) - (scale * ((float)Math.PI * 0.4F));

            this.head.y = 10.0F - (scale * 13.0F);
            this.head.z = -16.0F + (scale * 12.0F);
            this.head.xRot += (scale * ((float)Math.PI * 0.4F));
        }

        // 5. Walking (additive to the default pose)
        this.rightHindLeg.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
        this.leftHindLeg.xRot = Mth.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAmount;
        this.rightFrontLeg.xRot = Mth.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAmount;
        this.leftFrontLeg.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
    }

    @Override
    public ModelPart root() { return this.root; }
}