package unraveling.mechanics;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.research.ResearchItem;
import thaumcraft.api.research.ResearchPage;
import thaumcraft.api.research.ResearchPage.PageType;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import unraveling.UnravelingMod;

public class UResearchItem extends ResearchItem {

        public UResearchItem(String par1, String par2) {
                super(par1, par2);
        }

        public UResearchItem(String par1, String par2, AspectList tags, int par3,
                        int par4, int par5, ItemStack icon) {
                super(par1, par2, tags, par3, par4, par5, icon);
        }

        public UResearchItem(String par1, String par2, AspectList tags, int par3,
                        int par4, int par5, ResourceLocation icon) {
                super(par1, par2, tags, par3, par4, par5, icon);
        }

        @Override
        @SideOnly(Side.CLIENT)
        public String getName() {
                return StatCollector.translateToLocal("u.name." + key);
        }

        @Override
        @SideOnly(Side.CLIENT)
        public String getText() {
            return StatCollector.translateToLocal("u.text." + key);
        }

        @Override
        public ResearchItem setPages(ResearchPage... par) {
                for (ResearchPage page : par) {
	                	if (page.type == PageType.TEXT) {
	                        page.text = "u.page." + key + "." + page.text;
	                	}
                        if (page.type == PageType.INFUSION_CRAFTING) {
                                if (parentsHidden == null || parentsHidden.length == 0)
                                        parentsHidden = new String[] { "INFUSION" };
                                else {
                                        String[] newParents = new String[parentsHidden.length + 1];
                                        newParents[0] = "INFUSION";
                                        for (int i = 0; i < parentsHidden.length; i++)
                                                newParents[i + 1] = parentsHidden[i];
                                        parentsHidden = newParents;
                                }
                        }
                }
                return super.setPages(par);
        }
}