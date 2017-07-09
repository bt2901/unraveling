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
 
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import unraveling.mechanics.ExaminationData.Discovery;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.GameRegistry.UniqueIdentifier;
import java.lang.reflect.Type;

public class DiscoverySerializer implements JsonSerializer<Discovery>,  JsonDeserializer<Discovery> {
    public static final String KEY = "researchKey";
    public static final String FORCE = "overrideRequirements";
    public static final String ITEM = "item";

    public static final String MODID = "modid";

	@Override
	public JsonElement serialize(Discovery src, Type typeOfSrc, JsonSerializationContext context) {

		JsonObject result = new JsonObject();
        
		result.addProperty(KEY, src.researchKey);
		result.addProperty(FORCE, src.overrideRequirements);
        result.add(ITEM, context.serialize(src.item));

		return result;
	}
    @Override
    public Discovery deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (!json.isJsonObject()) {
            return null;
        }

        JsonObject jsonObject = json.getAsJsonObject();

        boolean override = jsonObject.has(FORCE) ? jsonObject.get(FORCE).getAsBoolean() : false;
        
        String key = jsonObject.get(KEY).getAsString();
        JsonElement elem = jsonObject.get(ITEM);
        System.out.println(elem);
        ItemStack item = null;
        if (elem != null && !elem.isJsonNull()) {
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.registerTypeAdapter(ItemStack.class, new ItemStackSerializer());
            Gson gson = gsonBuilder.create();

            item = gson.fromJson(elem, ItemStack.class);
            System.out.println(item);
        }
        return new Discovery(item, key, override);
    }
}
