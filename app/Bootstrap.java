import javax.inject.Inject;

import com.simian.medallion.service.ApplicationService;

import play.jobs.Job;

//@OnApplicationStart
public class Bootstrap extends Job {

	@Inject
	static ApplicationService applicationService;

	@Override
	public void doJob() {
		// List<Menu> menus = applicationService.listMenus();
		//
		// Map<String, String> programList = new HashMap<String, String>();
		// Map<String, Menu> urlList = new HashMap<String, Menu>();
		//
		// if (menus != null) {
		// for (Menu menu : menus) {
		// menu.setMenuBreadCrumb(getBreadCrumb(menu));
		// if (!Helper.isNullOrEmpty(menu.getMenuLink())) {
		// urlList.put(menu.getMenuLink(), menu);
		// }
		// if (!Helper.isNullOrEmpty(menu.getMenuNumber())) {
		// programList.put(menu.getMenuNumber(), menu.getMenuLink());
		// }
		// }
		// }
		//
		// Cache.set(UIConstants.CACHE_URLS, urlList);
		// Cache.set(UIConstants.CACHE_PROGRAMS, programList);
	}
}
