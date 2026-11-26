package net.candyperfumeboy.novamterram.entities.enhancements;

import net.candyperfumeboy.novamterram.util.IOcelotMixin;
import net.minecraft.client.model.OcelotModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.animal.Ocelot;

/** The vanilla ocelot model plus a sitting pose (the same one vanilla cats use). */
public class NTOcelotModel extends OcelotModel<Ocelot> {

    public NTOcelotModel(ModelPart root) {
        super(root);
    }

    public ModelPart headPart() {
        return this.head;
    }

    @Override
    public void prepareMobModel(Ocelot ocelot, float limbSwing, float limbSwingAmount, float partialTick) {
        // Vanilla resets every part position first, so nothing needs undoing on standing up
        super.prepareMobModel(ocelot, limbSwing, limbSwingAmount, partialTick);
        if (!((IOcelotMixin) ocelot).novamterram$isSitting()) return;

        this.body.xRot = (float) Math.PI / 4F;
        this.body.y -= 4.0F;
        this.body.z += 5.0F;
        this.head.y -= 3.3F;
        this.head.z += 1.0F;

        this.tail1.y += 8.0F;
        this.tail1.z -= 2.0F;
        this.tail2.y += 2.0F;
        this.tail2.z -= 0.8F;
        this.tail1.xRot = 1.7278761F;
        this.tail2.xRot = 2.670354F;

        this.leftFrontLeg.xRot = this.rightFrontLeg.xRot = -0.15707964F;
        this.leftFrontLeg.y = this.rightFrontLeg.y = 16.1F;
        this.leftFrontLeg.z = this.rightFrontLeg.z = -7.0F;

        this.leftHindLeg.xRot = this.rightHindLeg.xRot = (float) -Math.PI / 2F;
        this.leftHindLeg.y = this.rightHindLeg.y = 21.0F;
        this.leftHindLeg.z = this.rightHindLeg.z = 1.0F;

        this.state = SITTING_STATE; // tells setupAnim not to animate the legs
    }
}
