package unraveling.item;

import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import cpw.mods.fml.common.registry.GameData;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemBlock;
import cpw.mods.fml.common.Loader;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.GameRegistry.UniqueIdentifier;
import java.lang.reflect.Type;

public class ItemStackSerializer implements JsonSerializer<ItemStack>,  JsonDeserializer<ItemStack> {
    public static final String STACK_SIZE = "stackSize";
    public static final String ITEM_DAMAGE = "itemDamage";

    public static final String MODID = "modid";
    public static final String NAME = "name";

    public static final int DEFAULT_STACK_SIZE = 1;
    public static final int DEFAULT_ITEM_DAMAGE = 0;


	@Override
	public JsonElement serialize(ItemStack src, Type typeOfSrc, JsonSerializationContext context) {

		JsonObject result = new JsonObject();
        
		result.addProperty(STACK_SIZE, src.stackSize);
		result.addProperty(ITEM_DAMAGE, src.getItemDamage());
        UniqueIdentifier uid = GameRegistry.findUniqueIdentifierFor(src.getItem());
        result.addProperty(MODID, uid.modId);
        result.addProperty(NAME, uid.name);

		return result;
	}
    @Override
    public ItemStack deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        System.out.println(json);
        if (!json.isJsonObject()) {
            return null;
        }

        JsonObject jsonObject = json.getAsJsonObject();

        int stackSize = jsonObject.has(STACK_SIZE) ? jsonObject.get(STACK_SIZE).getAsInt() : DEFAULT_STACK_SIZE;
        int itemDamage = jsonObject.has(ITEM_DAMAGE) ? jsonObject.get(ITEM_DAMAGE).getAsInt() : DEFAULT_ITEM_DAMAGE;
        
        String modId = jsonObject.get(MODID).getAsString();
        String name = jsonObject.get(NAME).getAsString();
        System.out.println(modId + " : " + name + "loaded: " + Loader.isModLoaded(modId));

        if (!modId.equals("minecraft") && !Loader.isModLoaded(modId)) {
            return null;
        }

        Item item = GameRegistry.findItem(modId, name);
        System.out.println(item);
        return new ItemStack(item, stackSize, itemDamage);
    }

}
