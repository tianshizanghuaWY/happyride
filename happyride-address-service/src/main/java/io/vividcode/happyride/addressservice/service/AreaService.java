package io.vividcode.happyride.addressservice.service;

import io.vividcode.happyride.addressservice.dataaccess.AreaRepository;
import io.vividcode.happyride.addressservice.domain.Area;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class AreaService {

  @Autowired
  AreaRepository areaRepository;

  public Optional<AreaView> getArea(final Long areaCode, final int ancestorLevel) {
    return this.areaRepository.findByAreaCode(areaCode)
        .map(area -> AreaView
            .fromArea(area, this.getAreaWithHierarchy(area.getParentCode(), ancestorLevel)));
  }

  public List<AreaView> getAreaWithHierarchy(final Long areaCode, final int level) {
    return this.getAreaWithHierarchy(this.areaRepository.findByAreaCode(areaCode), level);
  }

  public List<AreaView> getAreaWithHierarchy(final Area area, final int level) {
    return this.getAreaWithHierarchy(Optional.ofNullable(area), level);
  }

  private List<AreaView> getAreaWithHierarchy(final Optional<Area> areaToFind, final int level) {
    int currentLevel = Math.max(level, 0);
    Optional<Area> currentArea = areaToFind;
    final List<Area> areas = new ArrayList<>();
    while (currentLevel-- > 0 && currentArea.isPresent()) {
      final Area area = currentArea.get();
      areas.add(area);
      currentArea = this.areaRepository.findByAreaCode(area.getParentCode());
    }
    return areas.stream().map(AreaView::fromArea).collect(Collectors.toList());
  }
}
