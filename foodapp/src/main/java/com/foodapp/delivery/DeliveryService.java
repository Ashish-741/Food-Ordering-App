package com.foodapp.delivery;

import com.foodapp.common.exception.*;
import com.foodapp.delivery.dto.DeliveryAgentDto;
import com.foodapp.order.*;
import com.foodapp.order.dto.OrderDto;
import com.foodapp.user.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeliveryService {

    private final DeliveryAgentRepository deliveryAgentRepository;
    private final UserRepository userRepository;
    private final OrderService orderService;

    @Transactional
    public DeliveryAgentDto registerAsAgent(String email, String vehicleType, String vehicleNumber) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        if (deliveryAgentRepository.findByUserId(user.getId()).isPresent()) {
            throw new BadRequestException("Already registered as delivery agent");
        }

        DeliveryAgent agent = DeliveryAgent.builder()
                .user(user)
                .vehicleType(vehicleType)
                .vehicleNumber(vehicleNumber)
                .build();

        return toDto(deliveryAgentRepository.save(agent));
    }

    @Transactional
    public DeliveryAgentDto toggleOnline(String email) {
        DeliveryAgent agent = getAgentByEmail(email);
        agent.setOnline(!agent.isOnline());
        return toDto(deliveryAgentRepository.save(agent));
    }

    @Transactional
    public void updateLocation(String email, double lat, double lng) {
        DeliveryAgent agent = getAgentByEmail(email);
        agent.setCurrentLat(lat);
        agent.setCurrentLng(lng);
        deliveryAgentRepository.save(agent);
    }

    @Transactional
    public OrderDto acceptDelivery(String email, Long orderId) {
        return orderService.updateOrderStatus(orderId, OrderStatus.PICKED_UP, email);
    }

    @Transactional
    public OrderDto updateDeliveryStatus(String email, Long orderId, OrderStatus status) {
        return orderService.updateOrderStatus(orderId, status, email);
    }

    private DeliveryAgent getAgentByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
        return deliveryAgentRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("DeliveryAgent", "userId", user.getId()));
    }

    private DeliveryAgentDto toDto(DeliveryAgent agent) {
        return DeliveryAgentDto.builder()
                .id(agent.getId())
                .name(agent.getUser().getName())
                .vehicleType(agent.getVehicleType())
                .vehicleNumber(agent.getVehicleNumber())
                .isOnline(agent.isOnline())
                .isAvailable(agent.isAvailable())
                .avgRating(agent.getAvgRating())
                .totalDeliveries(agent.getTotalDeliveries())
                .build();
    }
}
