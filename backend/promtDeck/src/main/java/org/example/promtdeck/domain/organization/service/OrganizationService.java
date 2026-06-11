package org.example.promtdeck.domain.organization.service;

import lombok.RequiredArgsConstructor;
import org.example.promtdeck.domain.organization.dto.request.OrganizationCreateRequest;
import org.example.promtdeck.domain.organization.dto.request.OrganizationMemberAddRequest;
import org.example.promtdeck.domain.organization.dto.response.OrganizationResponse;
import org.example.promtdeck.domain.organization.entity.Organization;
import org.example.promtdeck.domain.organization.entity.OrganizationMember;
import org.example.promtdeck.domain.organization.repository.OrganizationMemberRepository;
import org.example.promtdeck.domain.organization.repository.OrganizationRepository;
import org.example.promtdeck.domain.user.entity.User;
import org.example.promtdeck.domain.user.repository.UserRepository;
import org.example.promtdeck.global.common.ErrorCode;
import org.example.promtdeck.global.exception.CustomException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrganizationService {

    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;
    private final OrganizationMemberRepository organizationMemberRepository;

    @Transactional
    public OrganizationResponse create(Long userId, OrganizationCreateRequest request) {
        User user = getUser(userId);
        Organization organization = organizationRepository.save(Organization.create(request.name(), user));
        organizationMemberRepository.save(OrganizationMember.create(organization, user));

        return OrganizationResponse.from(organization);
    }

    @Transactional(readOnly = true)
    public List<OrganizationResponse> findAll(Long userId) {
        User user = getUser(userId);

        return organizationMemberRepository.findAllByUser(user)
                .stream()
                .map(OrganizationMember::getOrganization)
                .map(OrganizationResponse::from)
                .toList();
    }

    @Transactional
    public void addMember(Long userId, Long organizationId, OrganizationMemberAddRequest request) {
        User requester = getUser(userId);
        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new CustomException(ErrorCode.ORGANIZATION_NOT_FOUND));

        if (!organization.getOwner().getId().equals(requester.getId())) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        User member = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (!organizationMemberRepository.existsByOrganizationAndUser(organization, member)) {
            organizationMemberRepository.save(OrganizationMember.create(organization, member));
        }
    }

    public Organization getAccessibleOrganization(Long organizationId, User user) {
        if (organizationId == null) {
            return null;
        }

        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new CustomException(ErrorCode.ORGANIZATION_NOT_FOUND));

        if (!organizationMemberRepository.existsByOrganizationAndUser(organization, user)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        return organization;
    }

    public boolean canAccess(Organization organization, User user) {
        return organization != null && organizationMemberRepository.existsByOrganizationAndUser(organization, user);
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }
}
