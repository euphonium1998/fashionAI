package ices.fashion.service;

import ices.fashion.entity.collaborate.ColProject;
import ices.fashion.service.dto.collaborate.ColProjectDto;

import java.util.List;

public interface ColProjectService {
    public List<ColProjectDto> getProjectbyMemberId(int id);

    public ColProjectDto getProjectbyId(int id);

    public ColProject insertProject(Integer uid,String name,String description);

    public ColProject editProject(Integer pid,String name,String description);

    public int deleteProject(Integer pid);

    public int finishProject(Integer pid);
}
