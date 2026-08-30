package net.optifine;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.client.resources.DefaultResourcePack;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.ResourcePackRepository;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.ResourceLocation;

public class Config {
	
	private static GameSettings gameSettings = null;
	private static DefaultResourcePack defaultResourcePackLazy = null;
	private static final Logger LOGGER = LogManager.getLogger();
    private static Minecraft minecraft = Minecraft.getMinecraft();
    
    public static void initGameSettings(GameSettings p_initGameSettings_0_) {
        if (gameSettings == null) {
            gameSettings = p_initGameSettings_0_;
        }
    }

	public static int limit(int p_limit_0_, int p_limit_1_, int p_limit_2_) {
		if (p_limit_0_ < p_limit_1_) {
			return p_limit_1_;
		} else {
			return p_limit_0_ > p_limit_2_ ? p_limit_2_ : p_limit_0_;
		}
	}

	public static float limit(float p_limit_0_, float p_limit_1_, float p_limit_2_) {
		if (p_limit_0_ < p_limit_1_) {
			return p_limit_1_;
		} else {
			return p_limit_0_ > p_limit_2_ ? p_limit_2_ : p_limit_0_;
		}
	}

	public static double limit(double p_limit_0_, double p_limit_2_, double p_limit_4_) {
		if (p_limit_0_ < p_limit_2_) {
			return p_limit_2_;
		} else {
			return p_limit_0_ > p_limit_4_ ? p_limit_4_ : p_limit_0_;
		}
	}

	public static float limitTo1(float p_limitTo1_0_) {
		if (p_limitTo1_0_ < 0.0F) {
			return 0.0F;
		} else {
			return p_limitTo1_0_ > 1.0F ? 1.0F : p_limitTo1_0_;
		}
	}
	
	public static Object[] addObjectToArray(Object[] p_addObjectToArray_0_, Object p_addObjectToArray_1_) {
	    if (p_addObjectToArray_0_ == null) {
	        throw new NullPointerException("The given array is NULL");
	    } else {
	        int i = p_addObjectToArray_0_.length;
	        int j = i + 1;
	        Object[] aobject = (Object[]) Array.newInstance(p_addObjectToArray_0_.getClass().getComponentType(), j);
	        System.arraycopy(p_addObjectToArray_0_, 0, aobject, 0, i);
	        aobject[i] = p_addObjectToArray_1_;
	        return aobject;
	    }
	}

	public static Object[] addObjectToArray(Object[] p_addObjectToArray_0_, Object p_addObjectToArray_1_, int p_addObjectToArray_2_) {
	    List<Object> list = new ArrayList<Object>(Arrays.asList(p_addObjectToArray_0_));
	    list.add(p_addObjectToArray_2_, p_addObjectToArray_1_);
	    Object[] aobject = (Object[]) Array.newInstance(p_addObjectToArray_0_.getClass().getComponentType(), list.size());
	    return list.toArray(aobject);
	}

	public static Object[] addObjectsToArray(Object[] p_addObjectsToArray_0_, Object[] p_addObjectsToArray_1_) {
	    if (p_addObjectsToArray_0_ == null) {
	        throw new NullPointerException("The given array is NULL");
	    } else if (p_addObjectsToArray_1_.length == 0) {
	        return p_addObjectsToArray_0_;
	    } else {
	        int i = p_addObjectsToArray_0_.length;
	        int j = i + p_addObjectsToArray_1_.length;
	        Object[] aobject = (Object[]) Array.newInstance(p_addObjectsToArray_0_.getClass().getComponentType(), j);
	        System.arraycopy(p_addObjectsToArray_0_, 0, aobject, 0, i);
	        System.arraycopy(p_addObjectsToArray_1_, 0, aobject, i, p_addObjectsToArray_1_.length);
	        return aobject;
	    }
	}
	
	public static String[] tokenize(String p_tokenize_0_, String p_tokenize_1_) {
        StringTokenizer stringtokenizer = new StringTokenizer(p_tokenize_0_, p_tokenize_1_);
        List<String> list = new ArrayList<String>();

        while (stringtokenizer.hasMoreTokens()) {
            String s = stringtokenizer.nextToken();
            list.add(s);
        }

        String[] astring = (String[])list.toArray(new String[list.size()]);
        return astring;
    }
	
	public static String arrayToString(Object[] p_arrayToString_0_) {
        if (p_arrayToString_0_ == null) {
            return "";
        } else {
            StringBuffer stringbuffer = new StringBuffer(p_arrayToString_0_.length * 5);

            for (int i = 0; i < p_arrayToString_0_.length; ++i) {
                Object object = p_arrayToString_0_[i];

                if (i > 0) {
                    stringbuffer.append(", ");
                }

                stringbuffer.append(String.valueOf(object));
            }

            return stringbuffer.toString();
        }
    }

    public static String arrayToString(int[] p_arrayToString_0_) {
        if (p_arrayToString_0_ == null) {
            return "";
        } else {
            StringBuffer stringbuffer = new StringBuffer(p_arrayToString_0_.length * 5);

            for (int i = 0; i < p_arrayToString_0_.length; ++i) {
                int j = p_arrayToString_0_[i];

                if (i > 0) {
                    stringbuffer.append(", ");
                }

                stringbuffer.append(String.valueOf(j));
            }

            return stringbuffer.toString();
        }
    }
	
	public static int parseInt(String p_parseInt_0_, int p_parseInt_1_) {
        try {
            if (p_parseInt_0_ == null) {
                return p_parseInt_1_;
            } else {
                p_parseInt_0_ = p_parseInt_0_.trim();
                return Integer.parseInt(p_parseInt_0_);
            }
        } catch (NumberFormatException var3) {
            return p_parseInt_1_;
        }
    }
	
	public static float parseFloat(String p_parseFloat_0_, float p_parseFloat_1_) {
        try {
            if (p_parseFloat_0_ == null) {
                return p_parseFloat_1_;
            } else {
                p_parseFloat_0_ = p_parseFloat_0_.trim();
                return Float.parseFloat(p_parseFloat_0_);
            }
        } catch (NumberFormatException var3) {
            return p_parseFloat_1_;
        }
    }
	
	public static boolean parseBoolean(String p_parseBoolean_0_, boolean p_parseBoolean_1_) {
        try {
            if (p_parseBoolean_0_ == null) {
                return p_parseBoolean_1_;
            } else {
                p_parseBoolean_0_ = p_parseBoolean_0_.trim();
                return Boolean.parseBoolean(p_parseBoolean_0_);
            }
        } catch (NumberFormatException var3) {
            return p_parseBoolean_1_;
        }
    }
	
	public static boolean equals(Object p_equals_0_, Object p_equals_1_) {
        if (p_equals_0_ == p_equals_1_) {
            return true;
        } else {
            return p_equals_0_ == null ? false : p_equals_0_.equals(p_equals_1_);
        }
    }
	
	public static boolean isCustomItems() {
    	return gameSettings.customItemsOF;
    }
	
	public static int getUpdatesPerFrame() {
        return gameSettings.ofChunkUpdates;
    }
	
	public static void dbg(String p_dbg_0_) {
        LOGGER.info("[OptiFine] " + p_dbg_0_);
    }

    public static void warn(String p_warn_0_) {
        LOGGER.warn("[OptiFine] " + p_warn_0_);
    }

    public static void error(String p_error_0_) {
        LOGGER.error("[OptiFine] " + p_error_0_);
    }

    public static void log(String p_log_0_) {
        dbg(p_log_0_);
    }
    
    public static boolean hasResource(ResourceLocation p_hasResource_0_) {
        IResourcePack iresourcepack = getDefiningResourcePack(p_hasResource_0_);
        return iresourcepack != null;
    }
    
    public static IResourcePack getDefiningResourcePack(ResourceLocation p_getDefiningResourcePack_0_) {
        ResourcePackRepository resourcepackrepository = minecraft.getResourcePackRepository();
        IResourcePack iresourcepack = resourcepackrepository.getResourcePackInstance();

        if (iresourcepack != null && iresourcepack.resourceExists(p_getDefiningResourcePack_0_)) {
            return iresourcepack;
        } else {
            List<ResourcePackRepository.Entry> list = resourcepackrepository.repositoryEntries;

            for (int i = list.size() - 1; i >= 0; --i) {
                ResourcePackRepository.Entry resourcepackrepository$entry = list.get(i);
                IResourcePack iresourcepack1 = resourcepackrepository$entry.getResourcePack();

                if (iresourcepack1.resourceExists(p_getDefiningResourcePack_0_)) {
                    return iresourcepack1;
                }
            }

            if (getDefaultResourcePack().resourceExists(p_getDefiningResourcePack_0_)) {
                return getDefaultResourcePack();
            } else {
                return null;
            }
        }
    }
    
    public static String getResourcePackNames() {
        if (minecraft.getResourcePackRepository() == null) {
            return "";
        } else {
            IResourcePack[] airesourcepack = getResourcePacks();

            if (airesourcepack.length <= 0) {
                return getDefaultResourcePack().getPackName();
            } else {
                String[] astring = new String[airesourcepack.length];

                for (int i = 0; i < airesourcepack.length; ++i) {
                    astring[i] = airesourcepack[i].getPackName();
                }

                String s = arrayToString((Object[])astring);
                return s;
            }
        }
    }

    
    public static DefaultResourcePack getDefaultResourcePack() {
        if (defaultResourcePackLazy == null) {
            Minecraft minecraft = Minecraft.getMinecraft();
            defaultResourcePackLazy = minecraft.mcDefaultResourcePack;

            if (defaultResourcePackLazy == null) {
                ResourcePackRepository resourcepackrepository = minecraft.getResourcePackRepository();

                if (resourcepackrepository != null) {
                    defaultResourcePackLazy = (DefaultResourcePack)resourcepackrepository.rprDefaultResourcePack;
                }
            }
        }

        return defaultResourcePackLazy;
    }
    
    public static IResourcePack[] getResourcePacks() {
        ResourcePackRepository resourcepackrepository = minecraft.getResourcePackRepository();
        List<ResourcePackRepository.Entry> list = resourcepackrepository.getRepositoryEntries();
        List<IResourcePack> list1 = new ArrayList<IResourcePack>();

        for (Object resourcepackrepository$entry : list) {
            list1.add(((ResourcePackRepository.Entry) resourcepackrepository$entry).getResourcePack());
        }

        if (resourcepackrepository.getResourcePackInstance() != null) {
            list1.add(resourcepackrepository.getResourcePackInstance());
        }

        IResourcePack[] airesourcepack = (IResourcePack[])list1.toArray(new IResourcePack[list1.size()]);
        return airesourcepack;
    }
    
    public static Minecraft getMinecraft() {
        return minecraft;
    }
    
    public static TextureManager getTextureManager() {
        return minecraft.getTextureManager();
    }
    
    public static ModelManager getModelManager() {
        return minecraft.getRenderItem().modelManager;
    }
}
