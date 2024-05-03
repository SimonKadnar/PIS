import Vue from 'vue';
import VueRouter from 'vue-router';
import LoginPage from './components/LoginPage.vue';
import HomePage from './components/HomePage.vue';
import RoomsPage from './components/RoomsPage.vue';
import ServicesPage from './components/ServicesPage.vue';
import Parent from './components/Parent.vue';
import CustomersPage from './components/CustomersPage.vue';
import CustomerDetailPage from './components/CustomerDetailPage.vue';
import EmployeesPage from './components/EmployeesPage.vue';

import { library } from '@fortawesome/fontawesome-svg-core';
import { faEye,faEyeSlash } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome';
import ReservationsPage from './components/ReservationsPage.vue';
import ReservationForm from './components/ReservationForm.vue';
import AccommodatedCustomers from './components/AccommodatedCustomers.vue';

library.add(faEye, faEyeSlash);
Vue.component('font-awesome-icon', FontAwesomeIcon);

Vue.use(VueRouter);

const router = new VueRouter({
  mode: 'history',
  routes: [
    {
      path: '/Home',
      component: HomePage
    },
    {
      path: '/Home/login',
      component: LoginPage
    },
    {
      path: '/Home/Rooms',
      component: Parent,
      children: [
        {
          path: '',
          component: RoomsPage
        }
      ]
    },
    {
      path: '/Home/Services',
      component: Parent,
      children: [
        {
          path: '',
          component: ServicesPage
        }
      ]
    },
    {
      path: '/Home/Customers',
      component: Parent,
      children: [
        {
          path: '',
          component: CustomersPage
        }
      ]
    },
    {
    path: '/Home/Customers/detail/:personID',
    component: Parent,
    children: [
      {
        path: '',
        component: CustomerDetailPage
      }
    ]
    },
    {
      path: '/Home/Reservations',
      component: Parent,
      children: [
        {
          path: '',
          component: ReservationsPage
        }
      ]
    },
    {
      path: '/Home/Services',
      component: Parent,
      children: [
        {
          path: '',
          component: ReservationsPage
        }
      ]
    },

    {
      path: '/Home/CreateReservation',
      component: Parent,
      children: [
        {
          path: '',
          component: ReservationForm
        }
      ]
    },
    {
      path: '/Home/Employees',
      component: Parent,
      children: [
        {
          path: '',
          component: EmployeesPage
        }
      ]
    },

    {
      path: '/Home/AccommodatedCustomers',
      component: Parent,
      children: [
        {
          path: '',
          component: AccommodatedCustomers
        }
      ]
    }
  ]
});

new Vue({
  el: '#app',
  router,
  render: h => h('router-view') // Tento řádek říká Vue, aby vykreslovalo komponentu na základě URL adresy.
});
