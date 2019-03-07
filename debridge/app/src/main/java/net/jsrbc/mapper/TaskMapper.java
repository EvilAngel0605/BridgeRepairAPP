package net.jsrbc.mapper;

import net.jsrbc.R;
import net.jsrbc.entity.TblTask;
import net.jsrbc.frame.annotation.database.Param;
import net.jsrbc.frame.annotation.database.dml.Delete;
import net.jsrbc.frame.annotation.database.dml.Insert;
import net.jsrbc.frame.annotation.database.dml.Select;
import net.jsrbc.frame.annotation.database.dml.Update;

import java.util.List;

/**
 * Created by ZZZ on 2017-12-03.
 */
public interface TaskMapper {

    @Insert(R.string.addOrReplaceTaskList)
    void addOrReplaceTaskList(List<TblTask> taskList);

    @Update(R.string.updateTaskProgress)
    void updateTaskProgress(TblTask task);

    @Select(R.string.getTaskList)
    List<TblTask> getTaskList();

    @Delete(R.string.deleteTask)
    void deleteTask(@Param("taskId") String taskId);

    @Delete(R.string.deleteTaskBridge)
    void deleteTaskBridge(@Param("taskId") String taskId);

    @Delete(R.string.deleteTaskSide)
    void deleteTaskSide(@Param("taskId") String taskId);

    @Delete(R.string.deleteTaskSite)
    void deleteTaskSite(@Param("taskId") String taskId);

    @Delete(R.string.deleteTaskParts)
    void deleteTaskParts(@Param("taskId") String taskId);

    @Delete(R.string.deleteTaskMember)
    void deleteTaskMember(@Param("taskId") String taskId);
}
