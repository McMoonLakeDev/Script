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


package com.minecraft.moonlake.script;

import com.minecraft.moonlake.MoonLakePlugin;
import com.minecraft.moonlake.script.api.MoonLakeScriptLoader;
import com.minecraft.moonlake.script.listener.EventMapping;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public class ScriptPlugin extends JavaPlugin {

    private MoonLakeScriptLoader scriptLoader;

    public ScriptPlugin() {
    }

    @Override
    public void onEnable() {
        if(!setupMoonLake()) {
            this.getLogger().log(Level.SEVERE, "前置月色之湖核心API插件加载失败.");
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }
        // 初始化插件目录
        this.initFolder();
        // 初始化事件映射器
        EventMapping.INSTANCE.initalize();
        // 初始化脚本加载器以及进行加载
        this.scriptLoader = new MoonLakeScriptLoader(this);
        this.scriptLoader.loadScript();

        this.getLogger().info("月色之湖脚本 MoonLakeScript 插件 v" + getDescription().getVersion() + " 成功加载.");
    }

    @Override
    public void onDisable() {
        // 卸载所有脚本
        getScriptLoader().unloadAll();
    }

    private void initFolder() {
        if(!getDataFolder().exists())
            getDataFolder().mkdirs();
    }

    public MoonLakeScriptLoader getScriptLoader() {
        return scriptLoader;
    }

    private boolean setupMoonLake() {
        Plugin plugin = this.getServer().getPluginManager().getPlugin("MoonLake");
        return plugin != null && plugin instanceof MoonLakePlugin;
    }
}
