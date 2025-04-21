package com.saeed.ui;

import com.saeed.model.Employee;
import com.saeed.service.EmployeeService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

/**
 * View for listing all employees and providing options to add, edit, and delete employees.
 */
@Route(value = "", layout = MainLayout.class)
@PageTitle("Employees | Employee Management")
public class EmployeeListView extends VerticalLayout {

    private final EmployeeService employeeService;
    private final Grid<Employee> grid = new Grid<>(Employee.class);
    private final TextField filterText = new TextField();
    private final EmployeeForm form;

    public EmployeeListView(EmployeeService employeeService) {
        this.employeeService = employeeService;
        this.form = new EmployeeForm(this, employeeService);

        addClassName("list-view");
        setSizeFull();

        configureGrid();
        configureForm();

        add(getToolbar(), getContent());
        updateList();
        closeEditor();
    }

    private void configureGrid() {
        grid.addClassName("employee-grid");
        grid.setSizeFull();
        grid.setColumns("firstName", "lastName", "email", "department");
        grid.getColumns().forEach(col -> col.setAutoWidth(true));

        grid.asSingleSelect().addValueChangeListener(event ->
            editEmployee(event.getValue()));
    }

    private void configureForm() {
        form.setWidth("25em");
        form.setVisible(false);
    }

    private HorizontalLayout getToolbar() {
        filterText.setPlaceholder("Filter by name...");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> updateList());

        Button addEmployeeButton = new Button("Add Employee");
        addEmployeeButton.addClickListener(click -> addEmployee());

        HorizontalLayout toolbar = new HorizontalLayout(filterText, addEmployeeButton);
        toolbar.addClassName("toolbar");
        return toolbar;
    }

    private HorizontalLayout getContent() {
        HorizontalLayout content = new HorizontalLayout(grid, form);
        content.setFlexGrow(2, grid);
        content.setFlexGrow(1, form);
        content.addClassName("content");
        content.setSizeFull();
        return content;
    }

    private void updateList() {
        String filterValue = filterText.getValue().toLowerCase();
        if (filterValue.isEmpty()) {
            grid.setItems(employeeService.getAllEmployees());
        } else {
            grid.setItems(employeeService.getAllEmployees().stream()
                .filter(employee ->
                    containsIgnoreCase(employee.getFirstName(), filterValue) ||
                    containsIgnoreCase(employee.getLastName(), filterValue) ||
                    containsIgnoreCase(employee.getEmail(), filterValue) ||
                    containsIgnoreCase(employee.getDepartment(), filterValue))
                .toList());
        }
    }

    private boolean containsIgnoreCase(String value, String searchTerm) {
        return value != null && value.toLowerCase().contains(searchTerm);
    }

    private void addEmployee() {
        grid.asSingleSelect().clear();
        editEmployee(new Employee());
    }

    private void editEmployee(Employee employee) {
        if (employee == null) {
            closeEditor();
        } else {
            form.setEmployee(employee);
            form.setVisible(true);
            addClassName("editing");
        }
    }

    void closeEditor() {
        form.setEmployee(null);
        form.setVisible(false);
        removeClassName("editing");
    }

    void refreshGrid() {
        grid.select(null);
        updateList();
    }
}
