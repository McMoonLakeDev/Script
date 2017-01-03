/**
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

// 脚本插件唯一的插件对象 plugin

// 注册事件监听器: registerListener
// 返回值: boolean 是否注册成功
// 参数:
// event: 事件名称需要全路径, 例如 org.bukkit.event.player.PlayerJoinEvent
//            但是已对 bukkit 已经 spigot 的所有事件添加到映射器, 只需要事件名即可!
// priority: 事件的优先度 // TODO
// ignoreCancelled: 是否忽略已被阻止 // TODO
// handler: 处理函数 // TODO
//
plugin.registerListener({
    event: "AsyncPlayerChatEvent", // 监听异步玩家聊天事件
    handler: function (event) { // 处理
        var player = event.getPlayer(); // 获取玩家对象
        var message = event.getMessage(); // 获取聊天内容
        // 给玩家提示
        player.sendMessage("你发送了一条消息: " + message);
    }
});

// 脚本插件的卸载构造函数: setUnloadHook
// 返回值: N/A
// 参数:
// 处理函数
plugin.setUnloadHook(function () {
   plugin.getLogger().info("脚本成功已卸载.");
});

// TODO 更多添加中...
