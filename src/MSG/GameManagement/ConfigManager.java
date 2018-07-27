package MSG.GameManagement;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.bukkit.configuration.file.YamlConfiguration;

public class ConfigManager {
	
	File configFile;
	YamlConfiguration yamlConfig;
	
	public ConfigManager(File configFile){
		//setup the file to be used for config information
		this.configFile = configFile;
		
		// check if config file exists. if not, create one
		try{
			if (!configFile.exists()){
				System.out.println(configFile.getName() + " configuration file not found! Creating an empty one..");
				configFile.createNewFile();
			}
		} catch(Exception ex){
			System.out.println("Exception: " + ex.getMessage());
		}
		
		// load the yml config
		yamlConfig = YamlConfiguration.loadConfiguration(configFile);
	}
	
	public void setFile(File configFile){
		// set a new file to be used for config information
		this.configFile = configFile;
		
		// check if config file exists. if not, create one
		try{
			if (!configFile.exists()){
				System.out.println(configFile.getName() + " configuration file not found! Creating an empty one..");
				configFile.createNewFile();
			}
		} catch(Exception ex){
			System.out.println("Exception: " + ex.getMessage());
		}
		
		// load the yml config
		yamlConfig = YamlConfiguration.loadConfiguration(configFile);
	}
	
	public void set(String key, Object value){
		yamlConfig.set(key, value);
	}
	
	public void setList(String key, List<String> values){
		yamlConfig.set(key, values);
	}
	
	public String getString(String key){
		return yamlConfig.getString(key);
	}
	
	public int getInt(String key){
		return yamlConfig.getInt(key);
	}
	
	public double getDouble(String key){
		return yamlConfig.getDouble(key);
	}
	
	public boolean getBoolean(String key){
		return yamlConfig.getBoolean(key);
	}
	
	public List<String> getList(String key){
		return yamlConfig.getStringList(key);
	}
	
	public void save(){
		try {
			yamlConfig.save(configFile);
		} catch (IOException ex) {
			System.out.println("Error while saving " + configFile.getPath() + " Error: " + ex.getMessage());
		}
	}
	
	public void reload(){
		yamlConfig = YamlConfiguration.loadConfiguration(configFile);
	}

}
