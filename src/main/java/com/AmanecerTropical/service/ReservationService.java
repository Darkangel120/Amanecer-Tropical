package com.AmanecerTropical.service;

import com.AmanecerTropical.entity.Notification;
import com.AmanecerTropical.entity.Reservation;
import com.AmanecerTropical.entity.User;
import com.AmanecerTropical.entity.Flight;
import com.AmanecerTropical.entity.Hotel;
import com.AmanecerTropical.entity.Vehicle;
import com.AmanecerTropical.repository.ReservationRepository;
import com.AmanecerTropical.repository.UserRepository;
import com.AmanecerTropical.repository.PackageRepository;
import com.AmanecerTropical.repository.FlightRepository;
import com.AmanecerTropical.repository.HotelRepository;
import com.AmanecerTropical.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PackageRepository packageRepository;

    @Autowired
    private FlightRepository flightRepository;

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    public Optional<Reservation> getReservationById(@NonNull Long id) {
        return reservationRepository.findById(id);
    }

    public List<Reservation> getReservationsByUser(Long usuarioId) {
        return reservationRepository.findByUsuarioId(usuarioId);
    }

    public List<Reservation> getReservationsByDestination(Long paqueteId) {
        return reservationRepository.findByPaqueteId(paqueteId);
    }

    public List<Reservation> getReservationsByStatus(String estado) {
        return reservationRepository.findByEstado(estado);
    }

    public List<Reservation> getReservationsByUserAndStatus(Long usuarioId, String estado) {
        return reservationRepository.findByUsuarioIdAndEstado(usuarioId, estado);
    }

    public Reservation createReservation(@NonNull Reservation reservation) {
        // CARGAR LAS ENTIDADES COMPLETAS ANTES DE GUARDAR
        if (reservation.getUsuario() != null && reservation.getUsuario().getId() != null) {
            @SuppressWarnings("null")
            User usuario = userRepository.findById(reservation.getUsuario().getId())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            reservation.setUsuario(usuario);
        }

        if (reservation.getPaquete() != null && reservation.getPaquete().getId() != null) {
            @SuppressWarnings("null")
            com.AmanecerTropical.entity.Package paquete = packageRepository.findById(reservation.getPaquete().getId())
                    .orElseThrow(() -> new RuntimeException("Paquete no encontrado"));
            reservation.setPaquete(paquete);
        }

        if (reservation.getVuelo() != null && reservation.getVuelo().getId() != null) {
            @SuppressWarnings("null")
            Flight vuelo = flightRepository.findById(reservation.getVuelo().getId())
                    .orElseThrow(() -> new RuntimeException("Vuelo no encontrado"));
            reservation.setVuelo(vuelo);
        }

        if (reservation.getHotel() != null && reservation.getHotel().getId() != null) {
            @SuppressWarnings("null")
            Hotel hotel = hotelRepository.findById(reservation.getHotel().getId())
                    .orElseThrow(() -> new RuntimeException("Hotel no encontrado"));
            reservation.setHotel(hotel);
        }

        if (reservation.getVehiculo() != null && reservation.getVehiculo().getId() != null) {
            @SuppressWarnings("null")
            Vehicle vehiculo = vehicleRepository.findById(reservation.getVehiculo().getId())
                    .orElseThrow(() -> new RuntimeException("Vehículo no encontrado"));
            reservation.setVehiculo(vehiculo);
        }

        // Validar habitaciones y asientos si están especificados
        validateRoomsAndSeats(reservation);

        Reservation createdReservation = reservationRepository.save(reservation);

        String serviceDescription = buildServiceDescription(createdReservation);

        String message = "Tu " + serviceDescription + " ha sido realizada y espera confirmación.";

        Notification notification = new Notification(
            createdReservation.getUsuario(),
            "Nueva Reserva",
            message,
            "reservacion"
        );
        notificationService.saveNotification(notification);

        return createdReservation;
    }

    public Reservation updateReservation(@NonNull Reservation reservation) {
        // CARGAR LAS ENTIDADES COMPLETAS ANTES DE ACTUALIZAR
        if (reservation.getUsuario() != null && reservation.getUsuario().getId() != null) {
            @SuppressWarnings("null")
            User usuario = userRepository.findById(reservation.getUsuario().getId())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            reservation.setUsuario(usuario);
        }

        if (reservation.getPaquete() != null && reservation.getPaquete().getId() != null) {
            @SuppressWarnings("null")
            com.AmanecerTropical.entity.Package paquete = packageRepository.findById(reservation.getPaquete().getId())
                    .orElseThrow(() -> new RuntimeException("Paquete no encontrado"));
            reservation.setPaquete(paquete);
        }

        if (reservation.getVuelo() != null && reservation.getVuelo().getId() != null) {
            @SuppressWarnings("null")
            Flight vuelo = flightRepository.findById(reservation.getVuelo().getId())
                    .orElseThrow(() -> new RuntimeException("Vuelo no encontrado"));
            reservation.setVuelo(vuelo);
        }

        if (reservation.getHotel() != null && reservation.getHotel().getId() != null) {
            @SuppressWarnings("null")
            Hotel hotel = hotelRepository.findById(reservation.getHotel().getId())
                    .orElseThrow(() -> new RuntimeException("Hotel no encontrado"));
            reservation.setHotel(hotel);
        }

        if (reservation.getVehiculo() != null && reservation.getVehiculo().getId() != null) {
            @SuppressWarnings("null")
            Vehicle vehiculo = vehicleRepository.findById(reservation.getVehiculo().getId())
                    .orElseThrow(() -> new RuntimeException("Vehículo no encontrado"));
            reservation.setVehiculo(vehiculo);
        }

        Reservation updatedReservation = reservationRepository.save(reservation);

        String serviceDescription = buildServiceDescription(updatedReservation);

        if (reservation.getEstado().equals("confirmado")) {
            Notification notification = new Notification(
                updatedReservation.getUsuario(),
                "Reserva Confirmada",
                "Tu " + serviceDescription + " ha sido confirmada.",
                "reservacion"
            );
            notificationService.saveNotification(notification);
        } else if (reservation.getEstado().equals("cancelado")) {
            Notification notification = new Notification(
                updatedReservation.getUsuario(),
                "Reserva Cancelada",
                "Tu " + serviceDescription + " ha sido cancelada.",
                "reservacion"
            );
            notificationService.saveNotification(notification);
        }

        return updatedReservation;
    }

    public void deleteReservation(@NonNull Long id) {
        reservationRepository.deleteById(id);
    }

    public boolean isPackageAvailable(Long paqueteId, LocalDate fechaInicio, LocalDate fechaFin) {
        long overlappingCount = reservationRepository.countOverlappingReservations(paqueteId, null, null, null, fechaInicio, fechaFin);
        return overlappingCount == 0;
    }

    public boolean isFlightAvailable(Long vueloId, LocalDate fechaInicio, LocalDate fechaFin) {
        long overlappingCount = reservationRepository.countOverlappingReservations(null, vueloId, null, null, fechaInicio, fechaFin);
        return overlappingCount == 0;
    }

    public boolean isHotelAvailable(Long hotelId, LocalDate fechaInicio, LocalDate fechaFin) {
        long overlappingCount = reservationRepository.countOverlappingReservations(null, null, hotelId, null, fechaInicio, fechaFin);
        return overlappingCount == 0;
    }

    public boolean isVehicleAvailable(Long vehiculoId, LocalDate fechaInicio, LocalDate fechaFin) {
        long overlappingCount = reservationRepository.countOverlappingReservations(null, null, null, vehiculoId, fechaInicio, fechaFin);
        return overlappingCount == 0;
    }

    public List<Reservation> getOverlappingReservations(Long paqueteId, Long vueloId, Long hotelId, Long vehiculoId, LocalDate fechaInicio, LocalDate fechaFin) {
        return reservationRepository.findOverlappingReservations(paqueteId, vueloId, hotelId, vehiculoId, fechaInicio, fechaFin);
    }

    public List<Reservation> getRecentReservations(LocalDate fechaInicio) {
        return reservationRepository.findRecentReservations(fechaInicio);
    }

    private String buildServiceDescription(Reservation reservation) {
        List<String> services = new ArrayList<>();

        if (reservation.getPaquete() != null) {
            services.add("reservación del paquete " + reservation.getPaquete().getNombre());
        }
        if (reservation.getVuelo() != null) {
            services.add("vuelo de " + reservation.getVuelo().getOrigen() + " a " + reservation.getVuelo().getDestino());
        }
        if (reservation.getHotel() != null) {
            services.add("reservación del hotel " + reservation.getHotel().getNombre());
        }
        if (reservation.getVehiculo() != null) {
            services.add("reservación del vehículo " + reservation.getVehiculo().getNombre());
        }

        if (services.size() == 1) {
            return services.get(0);
        } else if (services.size() > 1) {
            return "reserva personalizada con " + String.join(", ", services);
        } else {
            return "servicio";
        }
    }

    private void validateRoomsAndSeats(Reservation reservation) {
        // Validar habitaciones si están especificadas
        if (reservation.getHabitacionesIds() != null && !reservation.getHabitacionesIds().trim().isEmpty()) {
            String[] roomIds = reservation.getHabitacionesIds().split(",");
            for (String roomIdStr : roomIds) {
                try {
                    @SuppressWarnings("unused")
                    Long roomId = Long.parseLong(roomIdStr.trim());
                    // Aquí podrías agregar validación adicional si es necesario
                } catch (NumberFormatException e) {
                    throw new RuntimeException("ID de habitación inválido: " + roomIdStr);
                }
            }
        }

        // Validar asientos si están especificados
        if (reservation.getAsientosIds() != null && !reservation.getAsientosIds().trim().isEmpty()) {
            String[] seatIds = reservation.getAsientosIds().split(",");
            for (String seatIdStr : seatIds) {
                try {
                    @SuppressWarnings("unused")
                    Long seatId = Long.parseLong(seatIdStr.trim());
                    // Aquí podrías agregar validación adicional si es necesario
                } catch (NumberFormatException e) {
                    throw new RuntimeException("ID de asiento inválido: " + seatIdStr);
                }
            }
        }
    }
}
