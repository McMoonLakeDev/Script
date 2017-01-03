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


package com.minecraft.moonlake.script.listener;

import com.minecraft.moonlake.exception.MoonLakeException;
import com.minecraft.moonlake.validate.Validate;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Pattern;

public final class EventMapping {

    private final Set<Plugin> pluginSet = new HashSet<>();
    private final Map<String, Class<? extends Event>> mappingMap = new HashMap<>();

    // 静态实例
    public final static EventMapping INSTANCE = new EventMapping();

    private EventMapping() {
    }

    public boolean initialized(String name) {
        // 获取指定事件是否已经初始化映射
        return mappingMap.containsKey(name);
    }

    public Class<? extends Event> getEventClass(String name) {
        // 获取指定事件的类对象
        Validate.isTrue(mappingMap.containsKey(name));
        return mappingMap.get(name);
    }

    public boolean initializePlugin(String pluginName) {
        // 初始化指定插件的所有可监听事件类到映射器
        Validate.notNull(pluginName, "The plugin name object is null.");
        return initializePlugin(Bukkit.getServer().getPluginManager().getPlugin(pluginName));
    }

    public boolean initializePlugin(Plugin plugin) {
        // 初始化指定插件的所有可监听事件类到映射器
        Validate.notNull(plugin, "The plugin object is null.");

        if(pluginSet.add(plugin)) {
            // 插件集合对象添加成功
            Class<?> clazz = plugin.getClass();
            URL path = clazz.getProtectionDomain().getCodeSource().getLocation();
            return initialize(clazz.getClassLoader(), path, "(.*?)\\.class");
        }
        return false;
    }

    public void initalize() {
        // 初始化事件映射器
        initializeBukkit();
        initializeSpigot();
    }

    protected boolean initializeBukkit() {
        // 初始化 bukkit 自带的可监听事件类到映射器
        Class<Bukkit> bukkitClass = Bukkit.class;
        URL path = bukkitClass.getProtectionDomain().getCodeSource().getLocation();
        return initialize(bukkitClass.getClassLoader(), path, "org/bukkit/event/(.*?)/(.*?)\\.class");
    }

    protected boolean initializeSpigot() {
        // 初始化 spigot 自带的可监听事件类到映射器
        // 先判断用户是否为 spigot 服务端
        Class<?> clazz = null;

        try {
            // 初始化 spigot 指定路径的一个类来判断
            clazz = Class.forName("org.spigotmc.SpigotConfig");
        } catch (Exception e) {
            // 异常表示类不存在则不是
            return false;
        }
        if(clazz == null) return false;
        // 否则初始化类成功表示服务端是 spigot 则进行初始化 spigot 自带的可监听事件类
        URL path = clazz.getProtectionDomain().getCodeSource().getLocation();
        return initialize(clazz.getClassLoader(), path, "org/spigotmc/event/(.*?)\\.class");
    }

    private boolean initialize(final ClassLoader classLoader, URL path, String regex) {
        // 初始化指定 URL 路径的所有可监听事件类到映射器
        Validate.isTrue(path.getProtocol().equals("file"));
        final Pattern pattern = Pattern.compile(regex);

        try {
            JarFile jarFile = new JarFile(path.getFile());
            jarFile.stream()
                    .filter((jarEntry -> pattern.matcher(jarEntry.getName()).matches()))
                    .forEach((jarEntry) -> initialize(classLoader, jarEntry));
            return true;
        } catch (Exception e) {
            throw new MoonLakeException(e.getMessage(), e);
        }
    }

    @SuppressWarnings("unchecked")
    private void initialize(ClassLoader classLoader, JarEntry jarEntry) {
        // 初始化指定的 jarEntry 对象到事件映射器
        try {
            String name = jarEntry.getName().replace('/', '.');
            Class<?> clazz = classLoader.loadClass(name.substring(0, name.length() - 6));
            // 验证类的属性
            Validate.isTrue(
                    Event.class.isAssignableFrom(clazz) && Modifier.isPublic(clazz.getModifiers()) && !Modifier.isAbstract(clazz.getModifiers()),
                    "The class '" + clazz.getName() + "' not is valid.");
            // 只有类是 Event 的子类并且是 public 并且不是抽象类则加载
            String finalName = clazz.getSimpleName();
            mappingMap.putIfAbsent(finalName, (Class<? extends Event>) clazz);
        } catch (Exception e) {
        }
    }

    @SuppressWarnings("unchecked")
    public static HandlerList getHandler(Class<? extends Event> clazz) {
        // 获取指定事件类的事件处理器
        try {
            // 获取事件类的 getHandlerList 函数
            Method method = clazz.getDeclaredMethod("getHandlerList");
            method.setAccessible(true);
            return (HandlerList) method.invoke(null);
        } catch (NoSuchMethodException e) {
            // 函数不存在则获取类的超类看看有没有
            Class<?> parent = clazz.getSuperclass();
            Class<? extends Event> parentEvent = parent != null && Event.class.isAssignableFrom(parent) ? (Class<? extends Event>) parent : null;
            if(parentEvent != null)
                // 超类不为 null 则获取
                return getHandler(parentEvent);
            // 否则异常
            throw new MoonLakeException(e.getMessage(), e);
        } catch (Exception e) {
            throw new MoonLakeException(e.getMessage(), e);
        }
    }
}
