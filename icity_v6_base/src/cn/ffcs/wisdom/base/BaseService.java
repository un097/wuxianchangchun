package cn.ffcs.wisdom.base;

import com.j256.ormlite.android.apptools.OrmLiteBaseService;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
/**
 * <p>Title: 基础Service      </p>
 * <p>Description: 
 *  基础服务，包括Sqlite
 * </p>
 * <p>@author: Eric.wsd                </p>
 * <p>Copyright: Copyright (c) 2012    </p>
 * <p>Company: FFCS Co., Ltd.          </p>
 * <p>Create Time: 2013-1-10             </p>
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public abstract class BaseService<H extends OrmLiteSqliteOpenHelper> extends OrmLiteBaseService<H> {

}
