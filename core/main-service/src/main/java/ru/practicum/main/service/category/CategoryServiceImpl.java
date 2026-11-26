package ru.practicum.main.service.category;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.dto.category.CategoryDto;
import ru.practicum.main.dto.category.NewCategoryDto;
import ru.practicum.main.exception.ConflictException;
import ru.practicum.main.exception.NotFoundException;
import ru.practicum.main.mapper.CategoryMapper;
import ru.practicum.main.model.Category;
import ru.practicum.main.repository.CategoryRepository;
import ru.practicum.main.repository.EventRepository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

    @Override
    public List<CategoryDto> getAllCategories(int from, int size) {
        PageRequest pageRequest = PageRequest.of(from / size, size);
        return categoryRepository.findAll(pageRequest)
                .stream()
                .map(CategoryMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDto getCategoryById(Long id) {
        Category category = findCategoryById(id);
        return CategoryMapper.toDto(category);
    }

    @Override
    @Transactional
    public CategoryDto createCategory(NewCategoryDto dto) {
        validateName(dto.name().trim());

        Category category = CategoryMapper.toEntity(dto);
        Category saved = categoryRepository.save(category);

        log.info("New category created. Id: {}, name: {}.", saved.getId(), saved.getName());
        return CategoryMapper.toDto(saved);
    }

    @Override
    @Transactional
    public CategoryDto updateCategory(Long id, NewCategoryDto dto) {
        Category category = findCategoryById(id);

        String name = dto.name().trim();
        if (!category.getName().equalsIgnoreCase(name)) {
            validateName(name);
        }

        category.setName(name);
        Category updated = categoryRepository.save(category);
        log.info("Category [id={}] name updated to: {}.", updated.getId(), updated.getName());
        return CategoryMapper.toDto(updated);
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        if (eventRepository.existsByCategoryId(id)) {
            throw new ConflictException("Cannot delete category assigned to an existing event.");
        }
        categoryRepository.deleteById(id);
        log.info("Category [id={}] deleted.", id);
    }

    private Category findCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Could not find category with id: " + id));
    }

    private void validateName(String name) {
        if (categoryRepository.existsByNameIgnoreCase(name)) {
            throw new ConflictException("Category '" + name + "' already exists.");
        }
    }
}