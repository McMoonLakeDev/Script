/*
 * Copyright (C) 2017 The MoonLake Authors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */


package com.minecraft.moonlake.script.api;

import com.minecraft.moonlake.script.ScriptPlugin;
import com.minecraft.moonlake.validate.Validate;

import javax.annotation.Nonnull;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public final class MoonLakeScriptLoader {

    private final ScriptPlugin main;
    private final Map<String, MoonLakeScript> scriptMap;
    private final File scriptDir;

    public MoonLakeScriptLoader(ScriptPlugin main) {
        this.main = main;
        this.scriptMap = new HashMap<>();
        this.scriptDir = new File(main.getDataFolder(), File.separator + "scripts");
        this.checkDir();
    }

    public ScriptPlugin getMain() {
        return main;
    }

    private void checkDir() {
        // 检测目录是否存在
        if(!scriptDir.exists())
            scriptDir.mkdirs();
    }

    public void loadScript() {
        // 加载脚本目录的所有脚本
        loadScript(scriptDir);
    }

    public void loadScript(String file) {
        // 加载指定脚本文件
        Validate.notNull(file, "The file object is null.");
        loadScript(new File(file));
    }

    public void loadScript(File file) {
        // 加载指定脚本文件
        Validate.notNull(file, "The file object is null.");
        Validate.isTrue(file.exists(), "The file object not exists.");

        File[] dataFiles = file.listFiles();

        if(dataFiles != null && dataFiles.length > 0) {
            // 不为 null 并且长度大于 0 则加载
            for(File dataFile : dataFiles) {
                // 循环遍历数据文件
                if(dataFile.exists() && dataFile.isDirectory()) {
                    // 存在并且则目录则继续加载
                    loadScript(dataFile);
                    continue;
                }
                if(dataFile.exists() && dataFile.isFile()) {
                    // 存在并且是文件则读取脚本
                    loadScriptFile(dataFile);
                }
            }
        }
    }

    private void loadScriptFile(@Nonnull File realFile) {
        // 加载指定脚本文件
        String fileName= realFile.getName();
        String scriptName = fileName.substring(fileName.lastIndexOf("."));
        ScriptEngine engine = new ScriptEngineManager().getEngineByName("javascript");
        MoonLakeScript script = new MoonLakeScript(getMain(), scriptName, engine);

        try {
            engine.put("plugin", script);
            engine.eval(new FileReader(realFile));
            // put 到 map 缓存
            scriptMap.put(scriptName, script);
            // 提示信息已成功加载
            getMain().getLogger().info("The script file '" + fileName + "' success loaded.");
        } catch (Exception e) {
            getMain().getLogger().log(Level.SEVERE, "The load script file '" + fileName + "' exception", e);
        }
    }

    public void unloadScript(String file) {
        // 卸载指定脚本文件
    }

    public void unloadScript(File file) {
        // 卸载指定脚本文件
    }

    public void unloadAll() {
        // 卸载全部的脚本文件
        scriptMap.values().forEach(MoonLakeScript::unload);
        scriptMap.clear();
    }
}
