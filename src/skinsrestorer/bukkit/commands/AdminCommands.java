/**
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 *
 */

package skinsrestorer.bukkit.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import skinsrestorer.bukkit.SkinsRestorer;
import skinsrestorer.shared.format.SkinProfile;
import skinsrestorer.shared.storage.LocaleStorage;
import skinsrestorer.shared.storage.SkinStorage;
import skinsrestorer.shared.utils.SkinFetchUtils;
import skinsrestorer.shared.utils.SkinFetchUtils.SkinFetchFailedException;

public class AdminCommands implements CommandExecutor {

	@Override
	public boolean onCommand(final CommandSender sender, Command cmd, String label, final String[] args) {
		if (!sender.hasPermission("skinsrestorer.cmds")) {
			sender.sendMessage("You don't have permission to do this");
			return true;
		}
		if (args.length == 0){
		    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&9Use '/skinsrestorer help' for help."));
			return true;
		}else
		if ((args.length == 1) && args[0].equalsIgnoreCase("help")){
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8]&7&m-------------&r&8[ &9SkinsRestorer Admin Help &8]&7&m-------------*r&8["));
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&9/skinsrestorer drop <player> &9-&a Drops player skin data."));
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&9/skinsrestorer update <player> &9-&a Updates player skin data."));
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&9/skinsrestorer set <player> <skin name> &9-&a Sets Player's skin."));
			return true;
		}else
		if ((args.length == 2) && args[0].equalsIgnoreCase("drop")) {
			SkinStorage.getInstance().removeSkinData(args[1]);
			sender.sendMessage(ChatColor.BLUE+"Skin data for player "+args[1]+" dropped");
			return true;
		} else
		if ((args.length == 1) && args[0].equalsIgnoreCase("savedata")) {
			SkinStorage.getInstance().saveData();
				sender.sendMessage(ChatColor.BLUE+"Skin data saved successfully.");
				return true;
	    } else
		if ((args.length == 2) && args[0].equalsIgnoreCase("update")) {
			SkinsRestorer.executor.execute(
				new Runnable() {
					@Override
					public void run() {
						String name = args[1];
						try {
							SkinStorage.getInstance().getOrCreateSkinData(name).attemptUpdate();
							sender.sendMessage(ChatColor.BLUE+"Skin data updated");
						} catch (SkinFetchFailedException e) {
							sender.sendMessage(ChatColor.RED+"Skin fetch failed: "+e.getMessage());
						}
					}
				}
			);
			return true;
		}
		if ((args.length == 3) && args[0].equalsIgnoreCase("set") || args[0].equalsIgnoreCase("change")) {
			SkinsRestorer.executor.execute(
				new Runnable() {
					@Override
					public void run() {
						String from = args[2];
						try {
							SkinProfile skinprofile = SkinFetchUtils.fetchSkinProfile(from, null);
							SkinStorage.getInstance().setSkinData(args[1], skinprofile);
							sender.sendMessage(ChatColor.BLUE+"You set "+args[1]+"'s skin.");
						} catch (SkinFetchFailedException e) {
							sender.sendMessage(ChatColor.RED+LocaleStorage.getInstance().PLAYER_SKIN_CHANGE_FAILED+e.getMessage());
						}
					}
				}
			);
			return true;
		}else
		  sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&9Use '/skinsrestorer help' for help."));
		return false;
	}

}