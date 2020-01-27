import Vue from 'vue';
import Router from 'vue-router';
import Home from '../components/Home.vue';
import NavBarNormal from '../components/NavBarNormal.vue';
import Footer from '../components/Footer.vue';
import NavBarMultiplayer from '../components/NavBarMultiplayer.vue';
import Playground from '../components/Playground.vue';
import TasksList from '../components/TasksList.vue';
import Multiplayer from '../components/Multiplayer.vue';
import Settings from '../components/Settings.vue';
import NavBarTask from '../components/NavBarTask.vue';
import Lobby from '../components/Lobby.vue';
import Room from '../components/Room.vue';
import MapEditor from '../components/MapEditor.vue';
import Task from '../components/Task.vue';
import TournamentsManagement from '../components/TournamentsManagement.vue';
import TournamentList from '../components/TournamentList.vue';
import TournamentWaiting from '../components/TournamentWaiting.vue';
import Tournament from '../components/Tournament.vue';
import TournamentsAdmin from '../components/TournamentsAdmin.vue';

Vue.use(Router);

const routes = [
  { path: '/', components: { default: Home, navbar: NavBarNormal, footer: Footer }, name: 'home' },
  { path: '/playground', components: { default: Playground, navbar: NavBarNormal, footer: Footer } },
  { path: '/tasks', components: { default: TasksList, navbar: NavBarNormal, footer: Footer } },
  { path: '/tasks/:id', components: { default: Task, navbar: NavBarTask, footer: Footer }, name: 'task' },
  { path: '/lobby', components: { default: Lobby, navbar: NavBarMultiplayer, footer: Footer }, name: 'lobby' },
  { path: '/tournaments', components: { default: TournamentList, navbar: NavBarMultiplayer, footer: Footer }, name: 'tournaments' },
  { path: '/tournament-wait/:id', components: { default: TournamentWaiting, navbar: NavBarMultiplayer, footer: Footer }, name: 'tournament-wait' },
  { path: '/tournament/:id', components: { default: Tournament, navbar: NavBarMultiplayer, footer: Footer }, name: 'tournament' },
  { path: '/admin/tournaments-management', components: { default: TournamentsManagement, navbar: NavBarNormal, footer: Footer }, name: 'tournamentsManagement' },
  { path: '/admin/tournament-view', components: { default: TournamentsAdmin, navbar: NavBarNormal, footer: Footer }, name: 'tournament-view' },
  { path: '/room/:id', components: { default: Room, navbar: NavBarMultiplayer, footer: Footer }, name: 'room' },
  { path: '/multiplayer', components: { default: Multiplayer, navbar: NavBarMultiplayer, footer: Footer }, name: 'multiplayer' },
  { path: '/settings', components: { default: Settings, navbar: NavBarNormal, footer: Footer } },
  { path: '/map-editor', components: { default: MapEditor, navbar: NavBarNormal, footer: Footer }, name: 'map-editor' },
];

export default new Router({ routes });
