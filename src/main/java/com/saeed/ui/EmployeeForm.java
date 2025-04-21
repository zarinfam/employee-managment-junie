package com.saeed.ui;

import com.saeed.model.Employee;
import com.saeed.service.EmployeeService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.shared.Registration;

import java.util.Arrays;
import java.util.List;

/**
 * Form for creating and editing employee records.
 */
public class EmployeeForm extends FormLayout {
    private final EmployeeService employeeService;
    private final EmployeeListView parentView;

    private TextField firstName = new TextField("First Name");
    private TextField lastName = new TextField("Last Name");
    private EmailField email = new EmailField("Email");
    private ComboBox<String> department = new ComboBox<>("Department");

    private Button save = new Button("Save");
    private Button delete = new Button("Delete");
    private Button cancel = new Button("Cancel");

    private Binder<Employee> binder = new BeanValidationBinder<>(Employee.class);
    private Employee employee;

    // List of common departments
    private static final List<String> DEPARTMENTS = Arrays.asList(
            "Engineering", "HR", "Finance", "Marketing", "Sales", "Operations", "IT", "Customer Support"
    );

    public EmployeeForm(EmployeeListView parentView, EmployeeService employeeService) {
        this.parentView = parentView;
        this.employeeService = employeeService;

        addClassName("employee-form");

        // Configure form fields
        department.setItems(DEPARTMENTS);

        // Bind fields to Employee properties
        binder.bindInstanceFields(this);

        add(
            firstName,
            lastName,
            email,
            department,
            createButtonsLayout()
        );
    }

    private Component createButtonsLayout() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        save.addClickShortcut(Key.ENTER);
        cancel.addClickShortcut(Key.ESCAPE);

        save.addClickListener(event -> validateAndSave());
        delete.addClickListener(event -> deleteEmployee());
        cancel.addClickListener(event -> parentView.closeEditor());

        binder.addStatusChangeListener(e -> save.setEnabled(binder.isValid()));

        return new HorizontalLayout(save, delete, cancel);
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
        binder.readBean(employee);

        // Only enable delete button if the employee exists (has an ID)
        delete.setEnabled(employee != null && employee.getId() != null);
    }

    private void validateAndSave() {
        if (binder.isValid()) {
            try {
                binder.writeBean(employee);

                if (employee.getId() == null) {
                    employeeService.createEmployee(employee);
                } else {
                    employeeService.updateEmployee(employee.getId(), employee);
                }

                parentView.refreshGrid();
                parentView.closeEditor();
            } catch (Exception e) {
                // Handle validation errors
                e.printStackTrace();
            }
        }
    }

    private void deleteEmployee() {
        if (employee != null && employee.getId() != null) {
            employeeService.deleteEmployee(employee.getId());
            parentView.refreshGrid();
            parentView.closeEditor();
        }
    }
}
