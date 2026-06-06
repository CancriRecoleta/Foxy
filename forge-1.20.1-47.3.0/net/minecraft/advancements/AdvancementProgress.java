//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.advancements;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;

public class AdvancementProgress implements Comparable<AdvancementProgress> {
    final Map<String, CriterionProgress> criteria;
    private String[][] requirements = new String[0][];

    private AdvancementProgress(Map<String, CriterionProgress> p_144358_) {
        this.criteria = p_144358_;
    }

    public AdvancementProgress() {
        this.criteria = Maps.newHashMap();
    }

    public void update(Map<String, Criterion> p_8199_, String[][] p_8200_) {
        Set<String> $$2 = p_8199_.keySet();
        this.criteria.entrySet().removeIf((p_8203_) -> {
            return !$$2.contains(p_8203_.getKey());
        });
        Iterator var4 = $$2.iterator();

        while(var4.hasNext()) {
            String $$3 = (String)var4.next();
            if (!this.criteria.containsKey($$3)) {
                this.criteria.put($$3, new CriterionProgress());
            }
        }

        this.requirements = p_8200_;
    }

    public boolean isDone() {
        if (this.requirements.length == 0) {
            return false;
        } else {
            String[][] var1 = this.requirements;
            int var2 = var1.length;

            for(int var3 = 0; var3 < var2; ++var3) {
                String[] $$0 = var1[var3];
                boolean $$1 = false;
                String[] var6 = $$0;
                int var7 = $$0.length;

                for(int var8 = 0; var8 < var7; ++var8) {
                    String $$2 = var6[var8];
                    CriterionProgress $$3 = this.getCriterion($$2);
                    if ($$3 != null && $$3.isDone()) {
                        $$1 = true;
                        break;
                    }
                }

                if (!$$1) {
                    return false;
                }
            }

            return true;
        }
    }

    public boolean hasProgress() {
        Iterator var1 = this.criteria.values().iterator();

        CriterionProgress $$0;
        do {
            if (!var1.hasNext()) {
                return false;
            }

            $$0 = (CriterionProgress)var1.next();
        } while(!$$0.isDone());

        return true;
    }

    public boolean grantProgress(String p_8197_) {
        CriterionProgress $$1 = (CriterionProgress)this.criteria.get(p_8197_);
        if ($$1 != null && !$$1.isDone()) {
            $$1.grant();
            return true;
        } else {
            return false;
        }
    }

    public boolean revokeProgress(String p_8210_) {
        CriterionProgress $$1 = (CriterionProgress)this.criteria.get(p_8210_);
        if ($$1 != null && $$1.isDone()) {
            $$1.revoke();
            return true;
        } else {
            return false;
        }
    }

    public String toString() {
        Map var10000 = this.criteria;
        return "AdvancementProgress{criteria=" + var10000 + ", requirements=" + Arrays.deepToString(this.requirements) + "}";
    }

    public void serializeToNetwork(FriendlyByteBuf p_8205_) {
        p_8205_.writeMap(this.criteria, FriendlyByteBuf::writeUtf, (p_144360_, p_144361_) -> {
            p_144361_.serializeToNetwork(p_144360_);
        });
    }

    public static AdvancementProgress fromNetwork(FriendlyByteBuf p_8212_) {
        Map<String, CriterionProgress> $$1 = p_8212_.readMap(FriendlyByteBuf::readUtf, CriterionProgress::fromNetwork);
        return new AdvancementProgress($$1);
    }

    @Nullable
    public CriterionProgress getCriterion(String p_8215_) {
        return (CriterionProgress)this.criteria.get(p_8215_);
    }

    public float getPercent() {
        if (this.criteria.isEmpty()) {
            return 0.0F;
        } else {
            float $$0 = (float)this.requirements.length;
            float $$1 = (float)this.countCompletedRequirements();
            return $$1 / $$0;
        }
    }

    @Nullable
    public String getProgressText() {
        if (this.criteria.isEmpty()) {
            return null;
        } else {
            int $$0 = this.requirements.length;
            if ($$0 <= 1) {
                return null;
            } else {
                int $$1 = this.countCompletedRequirements();
                return "" + $$1 + "/" + $$0;
            }
        }
    }

    private int countCompletedRequirements() {
        int $$0 = 0;
        String[][] var2 = this.requirements;
        int var3 = var2.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            String[] $$1 = var2[var4];
            boolean $$2 = false;
            String[] var7 = $$1;
            int var8 = $$1.length;

            for(int var9 = 0; var9 < var8; ++var9) {
                String $$3 = var7[var9];
                CriterionProgress $$4 = this.getCriterion($$3);
                if ($$4 != null && $$4.isDone()) {
                    $$2 = true;
                    break;
                }
            }

            if ($$2) {
                ++$$0;
            }
        }

        return $$0;
    }

    public Iterable<String> getRemainingCriteria() {
        List<String> $$0 = Lists.newArrayList();
        Iterator var2 = this.criteria.entrySet().iterator();

        while(var2.hasNext()) {
            Map.Entry<String, CriterionProgress> $$1 = (Map.Entry)var2.next();
            if (!((CriterionProgress)$$1.getValue()).isDone()) {
                $$0.add((String)$$1.getKey());
            }
        }

        return $$0;
    }

    public Iterable<String> getCompletedCriteria() {
        List<String> $$0 = Lists.newArrayList();
        Iterator var2 = this.criteria.entrySet().iterator();

        while(var2.hasNext()) {
            Map.Entry<String, CriterionProgress> $$1 = (Map.Entry)var2.next();
            if (((CriterionProgress)$$1.getValue()).isDone()) {
                $$0.add((String)$$1.getKey());
            }
        }

        return $$0;
    }

    @Nullable
    public Date getFirstProgressDate() {
        Date $$0 = null;
        Iterator var2 = this.criteria.values().iterator();

        while(true) {
            CriterionProgress $$1;
            do {
                do {
                    if (!var2.hasNext()) {
                        return $$0;
                    }

                    $$1 = (CriterionProgress)var2.next();
                } while(!$$1.isDone());
            } while($$0 != null && !$$1.getObtained().before($$0));

            $$0 = $$1.getObtained();
        }
    }

    public int compareTo(AdvancementProgress p_8195_) {
        Date $$1 = this.getFirstProgressDate();
        Date $$2 = p_8195_.getFirstProgressDate();
        if ($$1 == null && $$2 != null) {
            return 1;
        } else if ($$1 != null && $$2 == null) {
            return -1;
        } else {
            return $$1 == null && $$2 == null ? 0 : $$1.compareTo($$2);
        }
    }

    public static class Serializer implements JsonDeserializer<AdvancementProgress>, JsonSerializer<AdvancementProgress> {
        public Serializer() {
        }

        public JsonElement serialize(AdvancementProgress p_8226_, Type p_8227_, JsonSerializationContext p_8228_) {
            JsonObject $$3 = new JsonObject();
            JsonObject $$4 = new JsonObject();
            Iterator var6 = p_8226_.criteria.entrySet().iterator();

            while(var6.hasNext()) {
                Map.Entry<String, CriterionProgress> $$5 = (Map.Entry)var6.next();
                CriterionProgress $$6 = (CriterionProgress)$$5.getValue();
                if ($$6.isDone()) {
                    $$4.add((String)$$5.getKey(), $$6.serializeToJson());
                }
            }

            if (!$$4.entrySet().isEmpty()) {
                $$3.add("criteria", $$4);
            }

            $$3.addProperty("done", p_8226_.isDone());
            return $$3;
        }

        public AdvancementProgress deserialize(JsonElement p_8230_, Type p_8231_, JsonDeserializationContext p_8232_) throws JsonParseException {
            JsonObject $$3 = GsonHelper.convertToJsonObject(p_8230_, "advancement");
            JsonObject $$4 = GsonHelper.getAsJsonObject($$3, "criteria", new JsonObject());
            AdvancementProgress $$5 = new AdvancementProgress();
            Iterator var7 = $$4.entrySet().iterator();

            while(var7.hasNext()) {
                Map.Entry<String, JsonElement> $$6 = (Map.Entry)var7.next();
                String $$7 = (String)$$6.getKey();
                $$5.criteria.put($$7, CriterionProgress.fromJson(GsonHelper.convertToString((JsonElement)$$6.getValue(), $$7)));
            }

            return $$5;
        }
    }
}
