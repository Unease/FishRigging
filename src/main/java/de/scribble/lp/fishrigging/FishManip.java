package de.scribble.lp.fishrigging;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;

import de.scribble.lp.fishrigging.mixin.AccessorItemStack;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;

public class FishManip {
	private File fileLocation;
	private List<String> possibleItemNames=new ArrayList<String>(ImmutableList.of("cod","salmon","clownfish","pufferfish",
			"leather_boots","leather","bone","water_potion","string","fishing_rod_junk","bowl","stick","ink_sac","tripwire_hook","rotten_flesh",
			"waterlily","name_tag","saddle","bow","fishing_rod_treasure","book"));
	private List<String> possibleDamageItemNames=new ArrayList<String>(ImmutableList.of("leather_boots","fishing_rod_junk","bow","fishing_rod_trasure"));
	private List<String> possibleEnchantItemNames=new ArrayList<String>(ImmutableList.of("bow","fishing_rod_treasure","book"));
	
	public FishManip(File saveFile) {
		fileLocation=saveFile;
		createFile(saveFile);
	}
	public void createFile(File saveFile) {
		if (!saveFile.exists()) {
			StringBuilder toWrite= new StringBuilder();
			toWrite.append("#This file was generated by TASTools, the author is ScribbleLP. Leave blank to disable this feature. Everything starting with a hashtag is a comment\n"
					+ "#\n"
					+ "#Once an item has been caught, this file will update and remove the topmost item from your list\n"
					+ "#If there is an error, the file will show you which line it is\n"
					+ "#-----------------------------Possible items-----------------------------\n"
					+ "#(you can just delete the # at the start of the line if you want that item)\n"
					+ "#\n"
					+ "#Fishing category\n"
					+ "#cod\n"
					+ "#salmon\n"
					+ "#clownfish\n"
					+ "#pufferfish\n"
					+ "#\n"
					+ "#Junk Category\n"
					+ "#leather_boots;damage:0;   									#damage can be 0-58\n"
					+ "#leather\n"
					+ "#bone\n"
					+ "#water_potion\n"
					+ "#string\n"
					+ "#fishing_rod_junk;damage:0;   								#damage can be 0-57\n"
					+ "#bowl\n"
					+ "#stick\n"
					+ "#ink_sac;													#will always be 10 ink sacks at once\n"
					+ "#tripwire_hook\n"
					+ "#rotten_flesh\n"
					+ "#\n"
					+ "#Treasure Category\n"
					+ "#waterlily\n"
					+ "#name_tag\n"
					+ "#saddle\n"
					+ "#bow;damage:0;enchant:infinity[1],power[3];					#damage can be 0-96, add multiple enchantments seperated by a comma, number in [] shows the level\n"
					+ "#fishing_rod_treasure;damage:0;enchant:lure[1],unbreaking[3];	#damage can be 0-57\n"
					+ "#book;enchant:looting[3]");
			FileStuff.writeThings(toWrite, fileLocation, "Creating FishManip file");
		}
	}
	public List<String> readFile() throws IOException{
		createFile(fileLocation);
		return FileStuff.readThings(fileLocation);
	}
	
	public boolean isActive() {
		List<String> completeFile;
		try {
			completeFile = readFile();
		} catch (IOException e) {
			System.out.println("Failed to read file for FishManip for some reason");
			e.printStackTrace();
			return false;
		}
		int i=0;
		for (String line : completeFile) {
			if (line.startsWith("#")){
				continue;
			}
			i++;
			if(i==1) {
				if(line!=null&&!line.isEmpty()) {
					return true;
				}
			}
		}
		return false;
	}
	
	public ItemStack getItemFromTop() {
		List<String> completeFile;
		StringBuilder output = new StringBuilder();
		ItemStack item=null;
		boolean abortOverwriting=false;
		try {
			completeFile = readFile();
		} catch (IOException e) {
			System.out.println("Failed to read file for FishManip for some reason");
			e.printStackTrace();
			return null;
		}
		int i=0;
		for (String line : completeFile) {
			if (line.startsWith("#")){
				output.append(line+"\n");
				continue;
			}
			i++;
			Map<String, String> values=splitLine(line);
			if(i==1) {
				if(checkIfCorrect(values)) {
					item= convertMaptoItemStack(values);
					addEnchantments(item, values);
				}else {
					item= new ItemStack(Item.getByNameOrId("barrier"));
					item.setStackDisplayName("Something went wrong in FishRigging. Check your file");
					if(!line.contains("Mistake in this line ->")) {
						output.append("Mistake in this line -> "+line+"\n");
					}else {
						output.append(line+"\n");
					}
					abortOverwriting=true;
				}
				continue;
			}
			if(checkIfCorrect(values)) {
				output.append(line+"\n");
			}else {
				if(!line.contains("Mistake in this line ->")) {
					output.append("Mistake in this line -> "+line+"\n");
				}else {
					output.append(line+"\n");
				}
			}
		}
		if(item==null) {
			item= new ItemStack(Item.getByNameOrId("structure_void"));
			try {
				NBTTagCompound compound=JsonToNBT.getTagFromJson("{display:{Lore:[\"If you find this, please contact Scribble#1216 on discord since this is definitely a bug\"]}}");
				((AccessorItemStack)(Object)item).setStackTagCompund(compound);
			} catch (NBTException e) {
				e.printStackTrace();
			}
		}
		FileStuff.writeThings(output, fileLocation, "Rewriting fish_rigging.txt");
		return item;
	}
	private boolean checkIfCorrect(Map<String, String> values){
		boolean isCorrect=false;
		boolean continuing1=true;
		String item="";
		if(values!=null) {
			if(values.containsKey("item")) {
				for(String name: possibleItemNames) {
					if(values.containsValue(name)) {
						isCorrect=true;
						break;
					}
				}
				if(isCorrect) {
					item=values.get("item");
					
					if(item.contentEquals("leather_boots")&&!values.containsKey("damage")) {
						isCorrect=false;
						continuing1=false;
					}else if(item.contentEquals("fishing_rod_junk")&&!values.containsKey("damage")){
						isCorrect=false;
						continuing1=false;
					}else if(item.contentEquals("fishing_rod_treasure")&&(!values.containsKey("damage")||!values.containsKey("enchantment0"))) {
						isCorrect=false;
						continuing1=false;
					}else if(item.contentEquals("bow")&&(!values.containsKey("damage")||!values.containsKey("enchantment0"))) {
						isCorrect=false;
						continuing1=false;
					}else if(item.contentEquals("book")&&!values.containsKey("enchantment0")) {
						isCorrect=false;
						continuing1=false;
					}
				}
			}else {
				isCorrect=false;
			}
			if(continuing1) {
				if(values.containsKey("damage")) {
					boolean continuing=true;
					try {
						Integer.parseInt(values.get("damage"));
					} catch (NumberFormatException e) {
						isCorrect=false;
						continuing=false;
					}
					if(continuing) {
						for (String dmgname : possibleDamageItemNames) {
							if(item.contentEquals(dmgname)) {
								continuing=true;
								break;
							}else {
								continuing=false;
								isCorrect=false;
							}
						}
						if(continuing) {
							int damageval=Integer.parseInt(values.get("damage"));
							if (item.contentEquals("leather_boots")) {
								if(damageval>=0&&damageval<=58) {
									isCorrect=true;
								}else {
									isCorrect=false;
								}
							}else if (item.contentEquals("fishing_rod_junk")) {
								if(damageval>=0&&damageval<=57) {
									isCorrect=true;
								}else {
									isCorrect=false;
								}
							}else if (item.contentEquals("bow")) {
								if(damageval>=0&&damageval<=96) {
									isCorrect=true;
								}else {
									isCorrect=false;
								}
							}else if (item.contentEquals("fishing_rod_treasure")) {
								if(damageval>=0&&damageval<=16) {
									isCorrect=true;
								}else {
									isCorrect=false;
								}
							}
						}
					}
				}
				if(values.containsKey("enchantment0")) {
					int b=0;
					Enchantment testEnch;
					List<Enchantment> enchlist = new ArrayList<Enchantment>();
					while(values.containsKey("enchantment"+b)) {
						testEnch= Enchantment.getEnchantmentByLocation(values.get("enchantment"+b));
						boolean continuing=true;
						if(testEnch==null) {
							isCorrect=false;
							continuing=false;
						}
						try {
							Integer.parseInt(values.get("level"+b));
						} catch (NumberFormatException e) {
							isCorrect=false;
							continuing=false;
						}
						if(continuing) {
							ItemStack testStack =convertMaptoItemStack(values);
							int lvl=Integer.parseInt(values.get("level"+b));
							if (testEnch.canApply(testStack)&&testEnch.getMaxLevel()>=lvl&&testEnch.getMinLevel()<=lvl) {
								isCorrect=true;
							}else {
								if(values.containsValue("book")&&testEnch.getMaxLevel()>=lvl&&testEnch.getMinLevel()<=lvl){
									isCorrect=true;
								}else {
									isCorrect=false;
									break;
								}
							}
							for (Enchantment e:enchlist) {
								if(e.canApply(testStack)) {
									isCorrect=true;
								}else {
									isCorrect=false;
									break;
								}
							}
							enchlist.add(testEnch);
						}
						b++;
					}
				}
			}
		}else return false;
		return isCorrect;
		
	}
	private Map<String, String> splitLine(String line){
		Map<String, String> output= Maps.<String, String>newHashMap();
		
		String[] split1;
		String[] split2;
		String[] split3;
		String[] split4;
		boolean dmg=false;
		boolean cnt=false;
		boolean ench=false;
		
		if(line.contains(";")) {
			split1=line.split(";");
			output.put("item", split1[0]);
			for (int i = 0; i < split1.length; i++) {
				if(split1[i].startsWith("damage:")) {
					if(dmg) {
						return null;
					}
					split2=split1[i].split(":");
					output.put("damage",split2[1]);
					dmg=true;
				}else if(split1[i].startsWith("enchant:")){
					if(ench) {
						return null;
					}
					split2=split1[i].split(":");
					if(split2[1].contains(",")) {
						split3=split2[1].split(",");
						for (int j = 0; j < split3.length; j++) {
							if(split3[j].contains("[")) {
								split4=split3[j].split("\\[|\\]");
								output.put("enchantment"+j, split4[0]);
								output.put("level"+j,split4[1]);
							}else return null;
						}
					}else {
						if(split2[1].contains("[")) {
							split4=split2[1].split("\\[|\\]");
							output.put("enchantment0", split4[0]);
							output.put("level0",split4[1]);
						}else return null;
					}
					ench=true;
				}
			}
		}else {
			output.put("item", line);
		}
		return output;
	}
	
	private ItemStack convertMaptoItemStack(Map<String, String> values){
		String itemname= values.get("item");
		int damage=-1;
		if(values.containsKey("damage")) {
			damage= Integer.parseInt(values.get("damage"));
		}
		switch (itemname) {
		case "cod":
			return new ItemStack(Item.getByNameOrId("fish"), 1, 0);
		case "salmon":
			return new ItemStack(Item.getByNameOrId("fish"), 1, 1);
		case "clownfish":
			return new ItemStack(Item.getByNameOrId("fish"), 1, 2);
		case "pufferfish":
			return new ItemStack(Item.getByNameOrId("fish"), 1, 3);
		case "leather_boots":
			return new ItemStack(Item.getByNameOrId("leather_boots"), 1, damage);
		case "water_potion":
			try {
				ItemStack out=new ItemStack(Item.getByNameOrId("potion"), 1, 0);
				NBTTagCompound compound=JsonToNBT.getTagFromJson("{Potion:\"minecraft:water\"}");
				((AccessorItemStack)(Object)out).setStackTagCompund(compound);
				return out;
			} catch (NBTException e) {
				e.printStackTrace();
			}
		case "fishing_rod_junk":
			return new ItemStack(Item.getByNameOrId("fishing_rod"), 1, damage);
		case "ink_sac":
			return new ItemStack(Item.getByNameOrId("dye"), 10, 0);
		case "tripwire_hook":
			return new ItemStack(Item.getByNameOrId("tripwire_hook"));
		case "rotten_flesh":
			return new ItemStack(Item.getByNameOrId("rotten_flesh"));
		case "waterlily": 
			return new ItemStack(Blocks.WATERLILY);
		case "name_tag":
			return new ItemStack(Item.getByNameOrId("name_tag"));
		case "saddle":
			return new ItemStack(Item.getByNameOrId("saddle"));
		case "bow":
			return new ItemStack(Item.getByNameOrId("bow"), 1, damage);
		case "fishing_rod_treasure":
			return new ItemStack(Item.getByNameOrId("fishing_rod"), 1, damage);
		case "book":
			return new ItemStack(Item.getByNameOrId("enchanted_book"));
		default:
			return new ItemStack(Item.getByNameOrId(itemname));
		}
	}
	
	private ItemStack addEnchantments(ItemStack toEnchant, Map<String, String> values) {
		int b=0;
		String tag="";
		while(values.containsKey("enchantment"+b)) {
			if(!values.containsValue("book")) {
				toEnchant.addEnchantment(Enchantment.getEnchantmentByLocation(values.get("enchantment"+b)), Integer.parseInt(values.get("level"+b)));
			}else {
				if (b==0) {
					tag=tag+"{lvl:"+values.get("level"+b)+"s,id:"+Enchantment.getEnchantmentID(Enchantment.getEnchantmentByLocation(values.get("enchantment"+b)))+"s}";
				}else {
					tag=tag+",{lvl:"+values.get("level"+b)+"s,id:"+Enchantment.getEnchantmentID(Enchantment.getEnchantmentByLocation(values.get("enchantment"+b)))+"s}";
				}
			}
			b++;
		}
		if(values.containsValue("book")) {
			try {
				NBTTagCompound compound=JsonToNBT.getTagFromJson("{StoredEnchantments:["+tag+"]}");
				((AccessorItemStack)(Object)toEnchant).setStackTagCompund(compound);
			} catch (NBTException e) {
				e.printStackTrace();
			}
		}
		return toEnchant;
	}
}
