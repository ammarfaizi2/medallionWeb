package helpers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import play.mvc.Router;

import com.simian.medallion.constant.ExceptionConstants;
import com.simian.medallion.exception.MedallionException;
import com.simian.medallion.model.GnMenu;
import com.simian.medallion.model.comparators.MenuComparator;
import com.simian.medallion.model.helper.Helper;

public class MenuLoader {
	private static Logger log = Logger.getLogger(MenuLoader.class);

	public static void parseMenu(List<GnMenu> menuList, Map<String, GnMenu> urlList, Map<String, String> programList, Map<String, String> menuAccessList) {

		if (menuList != null) {
			// Setup JSON
			ObjectMapper json = new ObjectMapper();

			for (GnMenu menu : menuList) {
				if (menu.getMenuAccess() != null) {
					menuAccessList.put(menu.getMenuNumber(), menu.getMenuAccess());
				}
				menu.setMenuBreadCrumb(getBreadCrumb(menu));
				if (!Helper.isNullOrEmpty(menu.getMenuLink())) {
					urlList.put(menu.getMenuLink(), menu);
				}
				if (!Helper.isNullOrEmpty(menu.getMenuNumber())) {
					// programList.put(menu.getMenuNumber(),
					// menu.getMenuLink());
					if (menu.getMenuParams() == null) {
						Router.ActionDefinition action = Router.reverse(menu.getMenuAction());
						// logger.debug(Router.getFullUrl(menu.getMenuAction()));
						programList.put(menu.getMenuNumber(), action.url);
					} else {
						try {
							@SuppressWarnings("unchecked")
							Map<String, Object> params = json.readValue(menu.getMenuParams(), Map.class);
							Router.ActionDefinition action = Router.reverse(menu.getMenuAction(), params);
							log.debug("action:" + action.url);
							programList.put(menu.getMenuNumber(), action.url);
						} catch (JsonParseException e) {
							throw new MedallionException(ExceptionConstants.INVALID_DATA, e, "menu.menuParams");
						} catch (JsonMappingException e) {
							throw new MedallionException(ExceptionConstants.INVALID_DATA, e, "menu.menuParams");
						} catch (IOException e) {
							throw new MedallionException(ExceptionConstants.INVALID_DATA, e, "menu.menuParams");
						}
					}
				}
			}
			// Cache.add(UIConstants.CACHE_PGM, programList);
		}
	}

	public static String[] parseUserMenu(List<GnMenu> menuList) {
		log.debug("MENU LIST ===== " + menuList.size());
		Map<String, String> shortcuts = new HashMap<String, String>();
		StringBuffer accessList = new StringBuffer();
		List<GnMenu> tree = new ArrayList<GnMenu>();
		GnMenu root = null;
		GnMenu parent = null;
		if (menuList != null) {
			for (GnMenu menuItem : menuList) {
				// Set menu bread crumb
				if (menuItem != null) {
					menuItem.setMenuBreadCrumb(getBreadCrumb(menuItem));
					menuItem.setSubMenus(null);

					shortcuts.put(menuItem.getMenuNumber(), menuItem.getMenuBreadCrumb());

					if (!Helper.isNullOrEmpty(menuItem.getMenuAccess())) {
						accessList.append("|").append(menuItem.getMenuAccess());
					}

					if (menuItem.getParent() == null) {
						tree.add(menuItem);
						parent = null;
						root = null;
					} else {
						if ((parent != null) && (parent.getMenuKey() == menuItem.getParent().getMenuKey())) {
							parent.getSubMenus().add(menuItem);
						} else {
							parent = menuItem.getParent();
							parent.setSubMenus(new HashSet<GnMenu>(0));
							parent.getSubMenus().add(menuItem);

							if (parent != null) {
								if (parent.getParent() == null) {
									tree.add(parent);
								} else {
									if ((root != null) && (root.getMenuKey() == parent.getParent().getMenuKey())) {
										root.getSubMenus().add(parent);
									} else {
										if (root != null) {
											tree.add(root);
										}
										root = parent.getParent();
										root.setSubMenus(new HashSet<GnMenu>(0));
										root.getSubMenus().add(parent);
									}
								}
							}
						}
					}
				}
			}
		} else {
			shortcuts.put(null, null);
		}

		if (root != null) {
			if (parent != null) {
				root.getSubMenus().add(parent);
			}
			tree.add(root);
		} else if (parent != null) {
			tree.add(parent);
		}

		log.debug(">>> [JUN] " + shortcuts.keySet());
		List<String> keys = new ArrayList<String>(shortcuts.keySet());
		Collections.sort(keys);
		StringBuffer shortcutList = new StringBuffer();
		for (String key : keys) {
			shortcutList.append(",{label:'").append(key);
			shortcutList.append("',desc:'").append(shortcuts.get(key)).append("'}");
		}

		String[] result = new String[3];
		result[0] = constructMenu(tree);
		if (shortcutList.length() > 1) {
			result[1] = shortcutList.substring(1);
		}
		if (accessList.length() > 1) {
			result[2] = accessList.substring(1);
		}
		return result;
	}

	private static String getBreadCrumb(GnMenu menu) {
		StringBuffer buffer = new StringBuffer();
		if (menu != null) {
			if (menu.getParent() != null) {
				if (menu.getParent().getParent() != null) {
					buffer.append(menu.getParent().getParent().getMenuName()).append(" > ");
				}
				buffer.append(menu.getParent().getMenuName()).append(" > ");
			}
			buffer.append(menu.getMenuName());
		}
		return buffer.toString();
	}

	private static String constructMenu(List<GnMenu> menus) {
		// Setup JSON
		ObjectMapper json = new ObjectMapper();

		Collections.sort(menus, new MenuComparator());
		// String context = Play.configuration.getProperty("war.config", "");
		StringBuffer buffer = new StringBuffer();
		if (menus != null && menus.size() > 0) {
			buffer.append("<ul>");
			for (GnMenu menu : menus) {
				buffer.append("<li>");
				if (menu.getSubMenus() != null) {
					buffer.append("<a href=\"#\">");
					buffer.append(menu.getMenuName());
					buffer.append("</a>");

					buffer.append(constructMenu(new ArrayList<GnMenu>(menu.getSubMenus())));
				} else {
					// buffer.append("<a href=\"").append(context).append(menu.getMenuLink()).append("\">");
					// buffer.append("<a href=\"").append(menu.getMenuLink()).append("\">");
					buffer.append("<a href=\"");
					if (menu.getMenuParams() == null) {
						buffer.append(Router.getFullUrl(menu.getMenuAction()));
					} else {
						try {
							@SuppressWarnings("unchecked")
							Map<String, Object> params = json.readValue(menu.getMenuParams(), Map.class);
							buffer.append(Router.getFullUrl(menu.getMenuAction(), params));
						} catch (JsonParseException e) {
							throw new MedallionException(ExceptionConstants.INVALID_DATA, e, "menu.menuParams");
						} catch (JsonMappingException e) {
							throw new MedallionException(ExceptionConstants.INVALID_DATA, e, "menu.menuParams");
						} catch (IOException e) {
							throw new MedallionException(ExceptionConstants.INVALID_DATA, e, "menu.menuParams");
						}
					}
					buffer.append("\">");
					buffer.append(menu.getMenuName());
					buffer.append("</a>");
				}
				buffer.append("</li>");
			}
			buffer.append("</ul>");
		}

		return buffer.toString();
	}
}
