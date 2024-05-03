<template>
  <div class="table-container">
  <table style="width: 100%; border-collapse: collapse;">
  <thead>
  <tr>
  <th style="width: 100px;">Last Name*</th>
  <th style="width: 100px;">First Name*</th>
  <th style="width: 200px;">Email*</th>
  <th style="width: 200px;">Assignment*</th>
  <th style = "width: 80px;">Phone Preselection</th>
  <th style="width: 100px;">Phone Number*</th>
  <th style="width: 120px;">Document Number*</th>
  <th style="width: 120px;">Date of Birth*</th>
  <th style="width: 150px;">Password*</th>
  <th style="width: 50px;">Edit</th>
  </tr>
  </thead>
  <tbody>

        <tr v-if="addingNew">
          <td><input type="text" style="width: 100px;" v-model="newEmployee.LastName" placeholder="Last Name" :class="{ 'required-field-empty': newEmployee.LastName === '' }" required></td>
          <td><input type="text" style="width: 100px;" v-model="newEmployee.FirstName" placeholder="First Name" :class="{ 'required-field-empty': newEmployee.FirstName === '' }" required></td>
          <td><input type="email" style="width: 200px;" v-model="newEmployee.Email" placeholder="Email" :class="{ 'required-field-empty': newEmployee.Email === '' }" required></td>
          <select v-model="newEmployee.Assignment" style="width: 200px;" :class="{ 'required-field-empty': newEmployee.Assignment === '' }" required>
              <option value="Manager">Manager</option>
              <option value="Supervisor">Supervisor</option>
              <option value="Staff">Staff</option>
              <option value="Salesperson">Salesperson</option>
          </select>
          <td><input type="text" style="width: 80px;" v-model="newEmployee.PhonePreselection" placeholder="Phone Preselection"></td>
          <td><input type="number" style="width: 100px;" v-model="newEmployee.PhoneNumber" placeholder="Phone Number" :class="{ 'required-field-empty': newEmployee.PhoneNumber === '' }" required></td>
          <td><input type="text" style="width: 120px;" v-model="newEmployee.DocumentNumber" placeholder="Document Number" :class="{ 'required-field-empty': newEmployee.DocumentNumber === '' }" required></td>
          <td><input type="date" style="width: 120px;" v-model="newEmployee.DateOfBirth" placeholder="Date of Birth" :class="{ 'required-field-empty': newEmployee.DateOfBirth === '' }" required :max="getFormattedDateYears(18)"></td>
          <td><input type="password" style="width: 150px;" v-model="newEmployee.Password" placeholder="Password" :class="{ 'required-field-empty': newEmployee.Password === '' }" required></td>
          <td>
            <button @click="addNewEmployee" class="edit-button">OK</button>
            <button @click="cancelNewEmployee" class="delete-button">Cancel</button>
          </td>
        </tr>
        <tr v-else>
          <td colspan="5" style="text-align: center;">
            <button @click="toggleAddNew" class="edit-button">Add New</button>
          </td>
        </tr>
  
        <tr>
          <td><input type="text" style="width: 100px;" v-model="filters.LastName"></td>
          <td><input type="text" style="width: 100px;" v-model="filters.FirstName"></td>
          <td><input type="email" style="width: 200px;" v-model="filters.Email"></td>
          <td><input type="text" style="width: 200px;" v-model="filters.Assignment"></td>
          <td><input type="text" style="width: 80px;" v-model="filters.PhonePreselection"></td>
          <td><input type="number" style="width: 100px;" v-model="filters.PhoneNumber"></td>
          <td><input type="text" style="width: 120px;" v-model="filters.DocumentNumber"></td>
          <td></td>
          <td></td>
          <td></td>
        </tr>
  
        <tr v-for="employee in filteredEmployees" :key="employee.EmployeeID">
          <td v-if="!employee.editable">{{ employee.LastName }}</td>
          <td v-else><input type="text" style="width: 100px;" v-model="employee.LastName" :style="{ width: '150px' }" :class="{ 'required-field-empty': employee.LastName === '' }" required></td>
          <td v-if="!employee.editable">{{ employee.FirstName }}</td>
          <td v-else><input type="text" style="width: 100px;" v-model="employee.FirstName" :style="{ width: '150px'}" :class="{ 'required-field-empty': employee.FirstName === '' }" required></td>
          <td v-if="!employee.editable">{{ employee.Email }}</td>
          <td v-else><input type="email" style="width: 200px;" v-model="employee.Email" :style="{ width: '150px' }" :class="{ 'required-field-empty': employee.Email === '' }" required></td>
          <td v-if="!employee.editable">{{ employee.Assignment }}</td>
          <td v-else>
            <select v-model="employee.Assignment" style="width: 200px;" :class="{ 'required-field-empty': employee.Assignment === '' }" required>
              <option value="Manager">Manager</option>
              <option value="Supervisor">Supervisor</option>
              <option value="Staff">Staff</option>
              <option value="Salesperson">Salesperson</option>
            </select>
          </td>
          <td v-if="!employee.editable">{{ employee.PhonePreselection }}</td>
          <td v-else><input type="text" style="width: 80px;" v-model="employee.PhonePreselection" :style="{ width: '150px' }"></td>
          <td v-if="!employee.editable">{{ employee.PhoneNumber }}</td>
          <td v-else><input type="number" style="width: 100px;" v-model="employee.PhoneNumber" :style="{ width: '150px' }" :class="{ 'required-field-empty': employee.PhoneNumber === '' }" required></td>
          <td v-if="!employee.editable">{{ employee.DocumentNumber }}</td>
          <td v-else><input type="text" style="width: 120px;" v-model="employee.DocumentNumber" :style="{ width: '150px' }" :class="{ 'required-field-empty': employee.DocumentNumber === '' }" required></td>
          <td v-if="!employee.editable">{{ formatDate(employee.DateOfBirth) }}</td>
          <td v-else><input type="date" style="width: 120px;" v-model="employee.DateOfBirth" :style="{ width: '150px' }" :class="{ 'required-field-empty': employee.DateOfBirth === '' }" required></td>
          <td v-if="!employee.editable"></td>
          <td v-else><input type="password" style="width: 150px;" v-model="employee.Password" :style="{ width: '150px' }" :class="{ 'required-field-empty': employee.Password === '' }" required></td>
          <td>
            <button v-if="!employee.editable" class="edit-button" @click="toggleEdit(employee)">Edit</button>
            <button v-else class="ok-button" @click="updateEmployee(employee)">OK</button>
            <button v-if="employee.editable" class="delete-button" @click="deleteEmployee(employee)">Delete</button>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
  </template>

<script>
import Parent from './Parent.vue';  

export default {
  data() {
    return {
      addingNew: false,
      newEmployee: {
        LastName: '',
        FirstName: '',
        Email: '',
        Assignment: 'Salesperson',
        PhoneNumber: '777777777',
        DocumentNumber: '123456789',
        DateOfBirth: '1998-01-01',
        WorkShift: '2024-01-01',
        Password: '',
        PhonePreselection: '+120',
      },
      filters: {
        LastName: '',
        FirstName: '',
        Email: '',
        Assignment: '',
        PhoneNumber: '',
        DocumentNumber: '',
        PhonePreselection: '',
      },
      employees: [],
    };
  },
    mounted() {
        this.fetchEmployees();
    },
  computed: {
    filteredEmployees() {
      return this.employees.filter((employee) => {
        return (
        employee.LastName.toLowerCase().includes(this.filters.LastName.toLowerCase()) &&
        employee.FirstName.toLowerCase().includes(this.filters.FirstName.toLowerCase()) &&
        employee.Email.toLowerCase().includes(this.filters.Email.toLowerCase()) &&
        employee.Assignment.toLowerCase().includes(this.filters.Assignment.toLowerCase()) &&
        employee.PhonePreselection.toString().includes(this.filters.PhonePreselection.toString()) &&
        employee.PhoneNumber.toString().includes(this.filters.PhoneNumber.toString()) &&
        employee.DocumentNumber.toLowerCase().includes(this.filters.DocumentNumber.toLowerCase()) 
        );
      });
    },
  },
  methods: {
    fetchEmployees() {
      fetch('/Home/Employees/GetEmployees')
        .then(response => response.json())
        .then(data => {
          this.employees = data.map(employee => ({ ...employee, editable: false }));
        })
        .catch(error => {
          console.error('Error fetching employees:', error);
        });
    },
    toggleAddNew() {
      this.addingNew = true;
    },
    getFormattedDateYears(years = 0) {
      const today = new Date();
      today.setDate(today.getDate());
      const day = today.getDate().toString().padStart(2, '0');
      const month = (today.getMonth() + 1).toString().padStart(2, '0');
      const year = (today.getFullYear()-years);
      return `${year}-${month}-${day}`;
    },
    addNewEmployee() {
      if (this.validateNewEmployee()) {
        let maxId = Math.max(...this.employees.map(employee => employee.EmployeeID));
        this.newEmployee.EmployeeID = maxId + 1;
        this.employees.push({ ...this.newEmployee, editable: false});

        fetch('/Home/Employees/AddEmployee', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify(this.newEmployee),
        })
        .then(response => {
          if (response.ok) {
            console.log('Employee added successfully');
          } else {
            console.error('Error adding employee');
          }
        })
        .catch(error => {
          console.error('Error adding employee:', error);
        });
        this.clearNewEmployee();
        this.addingNew = false;
      }
      else {
        alert('Please fill in all required fields.');
      }
    },
    cancelNewEmployee() {
      this.clearNewEmployee();
      this.addingNew = false;
    },
    toggleEdit(employee) {
      employee.editable = !employee.editable;
    },
    formatDate(ReservDate) {
      if (!ReservDate) return ''; 

      const dateObj = new Date(ReservDate);
      const month = dateObj.getMonth() + 1;
      const day = dateObj.getDate();
      const year = dateObj.getFullYear();

      const formattedMonth = month < 10 ? `0${month}` : `${month}`;
      const formattedDay = day < 10 ? `0${day}` : `${day}`;

      return `${year}-${formattedMonth}-${formattedDay}`;
    },
    updateEmployee(employee) {
      // Validate and update employee data
      if (this.validateEmployee(employee)) {
        console.log('Employee updated:', employee);
        employee.editable = false;

        employee.DateOfBirth = this.formatDate(employee.DateOfBirth);
        employee.WorkShift = this.formatDate(employee.WorkShift);

        fetch('/Home/Employees/UpdateEmployee', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify(employee),
        })
        .then(response => {
          if (response.ok) {
            console.log('Employee updated successfully');
          } else {
            console.error('Error updating employee');
          }
        })
        .catch(error => {
          console.error('Error updating employee:', error);
        });
      }
        else {
            alert('Please fill in all required fields.');
        }
    },
    deleteEmployee(employee) {
      employee.editable = false;
      const index = this.employees.indexOf(employee);
      if (index !== -1) {
        this.employees.splice(index, 1);
      }
        fetch('/Home/Employees/DeleteEmployee', {
            method: 'POST',
            headers: {
            'Content-Type': 'application/json',
            },
            body: JSON.stringify(employee),
        })
        .then(response => {
            if (response.ok) {
            console.log('Employee deleted successfully');
            } else {
            console.error('Error deleting employee');
            }
        })
        .catch(error => {
            console.error('Error deleting employee:', error);
        });
    },
    clearNewEmployee() {
      this.newEmployee = {
        LastName: '',
        FirstName: '',
        Email: '',
        Assignment: '',
        PhoneNumber: '',
        DocumentNumber: '',
        DateOfBirth: '',
        WorkShift: '',
        Password: '',
        PhonePreselection: '',
      };
    },
    validateNewEmployee() {
      if (
        this.newEmployee.LastName === '' ||
        this.newEmployee.FirstName === '' ||
        this.newEmployee.Email === '' ||
        this.newEmployee.Assignment === '' ||
        this.newEmployee.PhoneNumber === '' ||
        this.newEmployee.DocumentNumber === '' ||
        this.newEmployee.DateOfBirth === '' ||
        this.newEmployee.Password === ''
      ) {
        return false;
      }
      return true;
    },
    validateEmployee(employee) {
      if (
        employee.LastName === '' ||
        employee.FirstName === '' ||
        employee.Email === '' ||
        employee.Assignment === '' ||
        employee.PhoneNumber === '' ||
        employee.DocumentNumber === '' ||
        employee.DateOfBirth === '' ||
        employee.Password === ''
      ) {
        return false;
      }
      return true;
    },
    getEmployeeInputWidth(value) {
      return value.length * 10 + 'px';
    },
  },
};
</script>
  
<style scoped>
.edit-button{
  padding: 10px 20px;
  margin-bottom: 10px;
  border: none;
  border-radius: 5px;
  background-color: #2196f3;
  color: white;
  font-size: 16px;
  cursor: pointer;
}
.edit-button:hover {
  background-color: #13568e;
}
.ok-button{
  padding: 10px 10px;
  margin-bottom: 10px;
  border: none;
  border-radius: 5px;
  background-color: #2196f3;
  color: white;
  font-size: 16px;
  cursor: pointer;
}
.ok-button:hover {
  background-color: #13568e;
}
input[type="date"],
input[type="time"],
input[type="email"],
input[type=number],
input[type="text"] {
  padding: 8px; 
  border: none; 
  border-radius: 4px; 
  font-size: 16px; 
  border: 1px solid #2196F3;
  }
input[type="date"],
input[type="time"],
input[type="email"],
input[type="text"],
input[type=number] {
  padding: 8px; 
  border: none; 
  border-radius: 4px; 
  font-size: 16px; 
  border: 1px solid #2196F3;
  }
input[type=number]
input[type="time"],
input[type="email"],
input[type="text"],
  input[type=date]{
    padding: 8px; 
  border: none; 
  border-radius: 4px; 
  font-size: 16px; 
  border: 1px solid #2196F3;
  }
input[type="number"]::-webkit-inner-spin-button,
input[type="number"]::-webkit-outer-spin-button {
  -webkit-appearance: inner-spin-button; 
  appearance: inner-spin-button;
  color: #2196F3;
  font-size: 16px; 
}
select{
  padding: 8px; 
  border: none; 
  border-radius: 4px; 
  font-size: 16px; 
  border: 1px solid #2196F3;
}
.delete-button{
  padding: 10px 10px;
  margin-bottom: 10px;
  border: none;
  border-radius: 5px;
  background-color: #f32f21;
  color: white;
  font-size: 16px;
  cursor: pointer;
}
.delete-button:hover {
  background-color: #951e16;
}

.required-field-empty {
    border-color: #ff0000 !important;
}

</style>
  