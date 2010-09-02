/* ========================================================================== *
 *                                   AdaptJ                                   *
 *              A Dynamic Application Profiling Toolkit for Java              *
 *                                                                            *
 *  Copyright (C) 2003-2004 Bruno Dufour                                      *
 *                                                                            *
 *  This software is under (heavy) development. Please send bug reports,      *
 *  comments or suggestions to bdufou1@sable.mcgill.ca.                       *
 *                                                                            *
 *  This library is free software; you can redistribute it and/or             *
 *  modify it under the terms of the GNU Library General Public               *
 *  License as published by the Free Software Foundation; either              *
 *  version 2 of the License, or (at your option) any later version.          *
 *                                                                            *
 *  This library is distributed in the hope that it will be useful,           *
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of            *
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU         *
 *  Library General Public License for more details.                          *
 *                                                                            *
 *  You should have received a copy of the GNU Library General Public         *
 *  License along with this library; if not, write to the                     *
 *  Free Software Foundation, Inc., 59 Temple Place - Suite 330,              *
 *  Boston, MA 02111-1307, USA.                                               *
 * ========================================================================== */

package adaptj_pool.event;

import java.io.*;

/**
 * An Event corresponding to the <code>JVMPI_THREAD_START</code> event. This event is triggered when a thread
 * is started in the Java VM.
 *
 * @author Bruno Dufour
 * @see ThreadEndEvent
 * @see <a href="http://java.sun.com/j2se/1.4/docs/guide/jvmpi/jvmpi.html">The Java Virtual Machine Profiler Interface (JVMPI)</a>
 */
public class ThreadStartEvent extends AdaptJEvent {
    /**
     * The name of the thread being started. The <code>thread_name</code> field in <code>ThreadStartEvent</code>
     * corresponds to the <code>thread_name</code> field in the <code>JVMPI_THREAD_START</code> event.
     */
    private String thread_name;
    /**
     * The name of the group to wich the thread belongs. The <code>group_name</code> field in <code>ThreadStartEvent</code>
     * corresponds to the <code>group_name</code> field in the <code>JVMPI_THREAD_START</code> event.
     */
    private String group_name;
    /**
     * The name of the parent of the thread being started. The <code>parent_name</code> field in <code>ThreadStartEvent</code>
     * corresponds to the <code>parent_name</code> field in the <code>JVMPI_THREAD_START</code> event.
     */
    private String parent_name;
    /**
     * The object ID of the thread being started. The <code>thread_id</code> field in <code>ThreadStartEvent</code>
     * corresponds to the <code>thread_id</code> field in the <code>JVMPI_THREAD_START</code> event.
     */
    private int thread_id;
    /**
     * The environment ID of the thread being started. The <code>thread_env_id</code> field in <code>ThreadStartEvent</code>
     * corresponds to the <code>thread_env_id</code> field in the <code>JVMPI_THREAD_START</code> event.
     */
    private int thread_env_id;
    
    public ThreadStartEvent() {
        this(null, null, null, 0, 0);
    }

    public ThreadStartEvent(String thread_name, String group_name, String parent_name,
            int thread_id, int thread_env_id) {
        setTypeID(ADAPTJ_THREAD_START);
        this.thread_name = thread_name;
        this.group_name = group_name;
        this.parent_name = parent_name;
        this.thread_id = thread_id;
        this.thread_env_id = thread_env_id;
    }
    
    /**
     * Get thread_name.
     *
     * @return thread_name as String.
     */
    public String getThreadName() {
        return thread_name;
    }
    
    /**
     * Set thread_name.
     *
     * @param thread_name the value to set.
     */
    public void setThreadName(String thread_name) {
        this.thread_name = thread_name;
    }
    
    /**
     * Get group_name.
     *
     * @return group_name as String.
     */
    public String getGroupName() {
        return group_name;
    }
    
    /**
     * Set group_name.
     *
     * @param group_name the value to set.
     */
    public void setGroupName(String group_name) {
        this.group_name = group_name;
    }
    
    /**
     * Get parent_name.
     *
     * @return parent_name as String.
     */
    public String getParentName() {
        return parent_name;
    }
    
    /**
     * Set parent_name.
     *
     * @param parent_name the value to set.
     */
    public void setParentName(String parent_name) {
        this.parent_name = parent_name;
    }
    
    /**
     * Get thread_id.
     *
     * @return thread_id as int.
     */
    public int getThreadID() {
        return thread_id;
    }
    
    /**
     * Set thread_id.
     *
     * @param thread_id the value to set.
     */
    public void setThreadID(int thread_id) {
        this.thread_id = thread_id;
    }
    
    /**
     * Get thread_env_id.
     *
     * @return thread_env_id as int.
     */
    public int getThreadEnvID() {
        return thread_env_id;
    }
    
    /**
     * Set thread_env_id.
     *
     * @param thread_env_id the value to set.
     */
    public void setThreadEnvID(int thread_env_id) {
        this.thread_env_id = thread_env_id;
    }
    
    public void readFromStream(DataInput in, short info) throws IOException {
        super.readFromStream(in, info);

        if ((info & ADAPTJ_FIELD_THREAD_NAME) != 0) {
            thread_name = in.readUTF();
        } else {
            thread_name = null;
        }
        
        if ((info & ADAPTJ_FIELD_GROUP_NAME) != 0) {
            group_name = in.readUTF();
        } else {
            group_name = null;
        }
        
        if ((info & ADAPTJ_FIELD_PARENT_NAME) != 0) {
            parent_name = in.readUTF();
        } else {
            parent_name = null;
        }
        
        if ((info & ADAPTJ_FIELD_THREAD_ID) != 0) {
            thread_id = in.readInt();
        } else {
            thread_id = 0;
        }
        
        if ((info & ADAPTJ_FIELD_THREAD_ENV_ID) != 0) {
            thread_env_id = in.readInt();
        } else {
            thread_env_id = 0;
        }
    }
}
