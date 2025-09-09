package com.sba301.quiz_service.service.Imp;

import com.sba301.quiz_service.dto.CategoryDTO;
import com.sba301.quiz_service.entity.Category;
import com.sba301.quiz_service.exception.CategoryNotFoundException;
import com.sba301.quiz_service.exception.InvalidQuizSubmissionException;
import com.sba301.quiz_service.repository.CategoryRepository;

import com.sba301.quiz_service.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CategoryServiceImp implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public List<CategoryDTO> getAllCategories() {

        List<Category> categories = categoryRepository.findAll();
        return categories.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CategoryDTO getCategoryById(Long id) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found with id: " + id));

        return convertToDTO(category);
    }

    public CategoryDTO updateCategory(Long id, CategoryDTO categoryDTO) {

        Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found with id: " + id));

        validateCategoryDTO(categoryDTO);

        Optional<Category> categoryWithSameName = categoryRepository.findByName(categoryDTO.getName());
        if (categoryWithSameName.isPresent() && !categoryWithSameName.get().getId().equals(id)) {
            throw new InvalidQuizSubmissionException("Category with name '" + categoryDTO.getName() + "' already exists");
        }

        existingCategory.setName(categoryDTO.getName());
        existingCategory.setDescription(categoryDTO.getDescription());

        Category savedCategory = categoryRepository.save(existingCategory);

        return convertToDTO(savedCategory);
    }

    private void validateCategoryDTO(CategoryDTO categoryDTO) {
        if (categoryDTO == null) {
            throw new InvalidQuizSubmissionException("Category data cannot be null");
        }

        if (categoryDTO.getName() == null || categoryDTO.getName().trim().isEmpty()) {
            throw new InvalidQuizSubmissionException("Category name cannot be empty");
        }

        if (categoryDTO.getName().length() > 100) {
            throw new InvalidQuizSubmissionException("Category name cannot exceed 100 characters");
        }

        if (categoryDTO.getDescription() != null && categoryDTO.getDescription().length() > 500) {
            throw new InvalidQuizSubmissionException("Category description cannot exceed 500 characters");
        }
    }

    private CategoryDTO convertToDTO(Category category) {
        CategoryDTO dto = new CategoryDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setDescription(category.getDescription());
        return dto;
    }
}