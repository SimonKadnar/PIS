<template>
  <div class="table-container">

    <!-- Date filter -->
    <label for="start">Start reservation date:</label>
    <input type="date" id="start" v-model="filters.startDate">
    <label for="end">End reservation date:</label>
    <input type="date" id="end" v-model="filters.endDate">

    <table>
      <thead>
        <tr>
          <th style="width: 50px;">Number*</th>
          <th style="width: 130px;">Type*</th>
          <th style="width: 50px;">Cost*</th>
          <th>Equip*</th>
          <th style="width: 130px;">State*</th>
          <th style="width: 50px;">Beds*</th>
          <th style="width: 50px;">Edit</th>
        </tr>
      </thead>
      <tbody>

        <!-- Add new -->
        <tr v-if="addingNew">
          <td><input type="number" min="1" style="width: 50px;" v-model="newRoom.RoomID" :class="{ 'required-field-empty': (CheckRoomID(newRoom.RoomID) || newRoom.RoomID === '') }" required></td>
          <td><input type="text" style="width: 130px;" v-model="newRoom.TypeRoom" :class="{ 'required-field-empty': newRoom.TypeRoom === '' }" required></td>
          <td><input type="number" min="0.5" step="0.5" style="width: 50px;" v-model="newRoom.Cost" :class="{ 'required-field-empty': newRoom.Cost === '' }" required></td>
          <td><input type="text" style="width: 95%;" v-model="newRoom.Equip" :class="{ 'required-field-empty': newRoom.Equip === '' }" required></td>
          <td> 
            <select v-model="newRoom.State" style="width: 130px;" :class="{ 'required-field-empty': newRoom.State === '' }" required>
              <option value="" disabled selected>Select State</option>
              <option value="Available">Available</option>
              <option value="Occupied">Occupied</option>
            </select>
          </td>
          <td><input type="number" min="1" style="width: 50px;" v-model="newRoom.Beds" :class="{ 'required-field-empty': newRoom.Beds === '' }" required></td>
          <td>
            <button @click="addNewRoom" class="edit-button">OK</button>
            <button @click="cancelNewRoom" class="delete-button">Cancel</button>
          </td>
        </tr>
        <tr v-else>
          <td colspan="6" style="text-align: center;">
            <button @click="() => { toggleAddNew(); newRoom.RoomID=Math.max(...this.rooms.map(room => room.RoomID))+1; }" class="edit-button">Add New</button>
          </td>
        </tr>


        <!-- Filter row -->
        <tr>
          <td><input type="text" v-model="filters.RoomID" style="width: 50px;"></td>
          <td><input type="text" v-model="filters.TypeRoom" style="width: 130px;"></td>
          <td><input type=number step="0.5" v-model="filters.Cost" style="width: 50px;"></td>
          <td><input type="text" style="width: 95%;" v-model="filters.Equip"></td>
          <td> 
            <select v-model="filters.State" style="width: 130px;">
              <option value="Available">Available</option>
              <option value="Occupied">Occupied</option>
              <option value=""> </option>
            </select>
          </td>
          <td><input type=number style="width: 50px;" v-model="filters.Beds"></td>
          <td></td> <!-- Empty cell for buttons -->
        </tr>


        <!-- Data rows -->
        <tr v-for="room in filteredRooms" :key="room.RoomID">
          <td>{{ room.RoomID }}</td>
          <td v-if="!room.editable">{{ room.TypeRoom }}</td>
          <td v-else><input type="text" style="width: 130px;" v-model="room.TypeRoom" :class="{ 'required-field-empty': room.TypeRoom === '' }" required></td>
          <td v-if="!room.editable">{{ room.Cost }}</td>
          <td v-else><input type= number step="0.5" v-model="room.Cost" style="width: 50px;" :class="{ 'required-field-empty': room.Cost === '' }" required></td>
          <td v-if="!room.editable">{{ room.Equip }}</td>
          <td v-else><input type="text" style="width: 95%;" v-model="room.Equip" :class="{ 'required-field-empty': room.Equip === '' }" required></td>
          <td v-if="!room.editable">{{ room.State }}</td>
          <td v-else> 
            <select v-model="room.State" style="width: 130px;" :class="{ 'required-field-empty': room.State === '' }" required>
              <option value="Available">Available</option>
              <option value="Occupied">Occupied</option>
            </select>
          </td>
          <td v-if="!room.editable">{{ room.Beds }}</td>
          <td v-else><input type= number min="1" style="width: 50px;" v-model="room.Beds" :class="{ 'required-field-empty': room.Beds === '' }" required></td>
          <td>
            <button v-if="!room.editable" class="edit-button" @click="toggleEdit(room)">Edit</button>
            <button v-else class="ok-button" @click="updateRoom(room)">OK</button>
            <button v-if="room.editable && RezeravationForRoom(room.RoomID)" class="delete-button" @click="deleteRoom(room)">Delete</button>  
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
      rooms: [],
      reservations: [],
      filters: {
        RoomID: '',
        TypeRoom: '',
        Cost: '',
        Equip: '',
        State: '',
        Beds: '',
        startDate: '', 
        endDate: '',   
      },
      addingNew: false,
      newRoom: {    //preloaded data for new room
        RoomID: '458',
        TypeRoom: 'Double',
        Cost: '15.0',
        Equip: 'Desk, Chair, Wardrobe, Bed',
        State: 'Available',
        Beds: '2'
      }
    };
  },
  mounted() {
    this.fetchRooms(); // load rooms
    this.fetchReservations();  //load rezervations
  },
  computed: {
    
    filteredRooms() {
      // filter rooms if some value is not set filter will ignore that value
      return this.rooms.filter(room => {
        return (
          room.RoomID.toString().includes(this.filters.RoomID) &&
          room.TypeRoom.toLowerCase().includes(this.filters.TypeRoom.toLowerCase()) &&
          room.Cost.toString().includes(this.filters.Cost) &&
          room.Equip.toLowerCase().includes(this.filters.Equip.toLowerCase()) &&
          room.Beds.toString().includes(this.filters.Beds) &&
          // filter by dates, if is not set satrt and end result is true
          ((this.filters.startDate && this.filters.endDate) ? this.checkRoomAvailable(room.RoomID, this.reservations, this.filters.startDate, this.filters.endDate) : true ) &&
          room.State.toLowerCase().includes(this.filters.State.toLowerCase())
        );
      });
    }
  },
  methods: {
    // if room is reservet between startDate and endDate = false otherwise true
    checkRoomAvailable(roomID, reservations, startDate, endDate) {
      console.log("CHECK DATE FOR ROOM");
      
      const parts = startDate.split('-');
      const year = parseInt(parts[0], 10);
      const month = parseInt(parts[1], 10) - 1; // Months are in JavaScript zero-indexed
      const day = parseInt(parts[2], 10);
      const Startdate = new Date(year, month, day).getTime();

      const parts1 = endDate.split('-');
      const year1 = parseInt(parts1[0], 10);
      const month1 = parseInt(parts1[1], 10) - 1;
      const day1 = parseInt(parts1[2], 10);
      const EndDate = new Date(year1, month1, day1).getTime();

      for (const reservation of reservations) 
      {
        if (
          roomID === reservation.RoomID &&
          EndDate >= reservation.Start && 
          Startdate <= reservation.End) 
        {
          return false;
        }
      }
      return true;
    },

    //if room have some reservation
    RezeravationForRoom(roomID){
      for (const reservation of this.reservations) 
      {
        if (roomID === reservation.RoomID) 
        {
          return false;
        }
      }
      return true;
    },

    //if room whit RoomID already exists
    CheckRoomID(ID){
      for (const room of this.rooms) 
      {
        const numericID = parseInt(ID);
        const numericRoomID = parseInt(room.RoomID);
        if (numericID === numericRoomID) 
        {
          return true;
        }
      }
      return false;
    },

    // requesting for room data
    async fetchRooms() {
      fetch('/Home/Rooms/GetRooms') 
        .then(response => response.json())
        .then(data => {
          this.rooms = data.map(room => ({ ...room, editable: false }));
        })
        .catch(error => {
          console.error('Error fetching rooms:', error);
        });
    },
    toggleEdit(room) {
      room.editable = !room.editable;
    },

    // requesting for reservation data
    async fetchReservations() {
      try {
        const response = await fetch('/Home/Reservations/GetReservations');
        this.reservations = await response.json();
      } catch (error) {
        console.error('Error fetching reservations:', error);
      }
    },

    updateRoom(room) {
      if (room.TypeRoom && room.Cost && room.Equip && room.State && room.Beds) 
      {
        console.log('Updating room:', room);
        room.editable = false; //close edit window

        //send data to server
        fetch('/Home/Rooms/UpdateRoom', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify(room),
        })
        .then(response => {
          if (response.ok) {
            console.log('Room updated successfully');
          } else {
            throw new Error('Failed to update Room');
          }
        })
        .catch(error => {
          console.error('Error updating room:', error);
        });
      }
      else 
      {
        alert('Fill in all red fields for new Room.');
      }
    },
    
    deleteRoom(room) {
      console.log('Deleting room:', room);
      room.editable = false; //close edit window

      const index = this.rooms.indexOf(room);
      if (index !== -1) 
      {
        this.rooms.splice(index, 1);
      }
      //send data to remove room
      fetch('/Home/Rooms/DeleteRoom', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(room),
      })
      .then(response => {
        if (response.ok) {
          console.log('Room deleted successfully');
        } else {
          throw new Error('Failed to delete Room');
        }
      })
      .catch(error => {
        console.error('Error deleting room:', error);
      });
    },

    toggleAddNew() {
      this.addingNew = true;
    },

    addNewRoom() 
    {
      if (this.CheckRoomID(this.newRoom.RoomID))
        alert('Room whit this number already exists');
      else
      //check if some input is missing
      if (this.newRoom.TypeRoom && this.newRoom.Cost && this.newRoom.Equip && this.newRoom.State && this.newRoom.Beds) 
      {
        this.rooms.push({ ...this.newRoom, editable: false });
        
        //send data for new room
        fetch('/Home/Rooms/AddRoom', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify(this.newRoom),
        })
        .then(response => {
          if (response.ok) {
            console.log('Room added successfully');
          } else {
            throw new Error('Failed to add room');
          }
        })
        .catch(error => {
          console.error('Error adding room:', error);
        });

        //preloaded data
        this.newRoom = {
          RoomID: '',
          TypeRoom: 'Double',
          Cost: '15.0',
          Equip: 'Desk, Chair, Wardrobe, Bed',
          State: 'Available',
          Beds: '2'
        };
        //set new room non-existent id
        let maxRoomID = Math.max(...this.rooms.map(room => room.RoomID));
        this.newRoom.RoomID = maxRoomID + 1;
        this.addingNew = false;
      } 
      else 
      {
        alert('Fill in all red fields for new Room.');
      }
    },
    //cancel add
    cancelNewRoom() {
      this.newRoom = {
        RoomID: '',
        TypeRoom: 'Double',
        Cost: '15.0',
        Equip: 'Desk, Chair, Wardrobe, Bed',
        State: 'Available',
        Beds: '2'
      };
      //set new room non-existent id
      let maxRoomID = Math.max(...this.rooms.map(room => room.RoomID));
      this.newRoom.RoomID = maxRoomID + 1;
      this.addingNew = false;
    }
  }
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
input[type="text"] {
  padding: 8px; 
  border: none; 
  border-radius: 4px; 
  font-size: 16px; 
  border: 1px solid #2196F3;
  }
input[type=number] {
  padding: 8px; 
  border: none; 
  border-radius: 4px; 
  font-size: 16px; 
  border: 1px solid #2196F3;
  }
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