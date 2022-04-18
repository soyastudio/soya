package soya.application.albertsons.domains;

import soya.framework.erm.*;

@DomainContext(name = "OfferRequest")
public interface OfferRequestDomain {

    class OfferRequest {

        private String additionalDetailsTxt;

        private String advertisementTypeCd;

        private String advertisementTypeDsc;

        private String advertisementTypeShortDsc;

        private String applicationId;

        private String brandInfoTxt;

        private String businessJustificationTxt;

        private String customerSegmentInfoTxt;

        private String deliveryChannelTypeCd;

        private String deliveryChannelTypeDsc;

        private String offerBankTypeCd;

        private String offerBankId;

        private String offerBankNm;

        private String templateId;

        private String templateNm;

        private String disclaimerTxt;

        private String displayEndDt;

        private String displayStartDt;

        private String groupId;

        private String imageId;

        private String manufacturerTypeCreateFileDt;

        private String manufacturerTypeCreateFileTs;

        private String manufacturerTypeDestinationId;

        private String manufacturerTypeDsc;

        private String manufacturerTypeFileNm;

        private String manufacturerTypeFileSequenceNbr;

        private String manufacturerTypeId;

        private String manufacturerTypeIdTxt;

        private String manufacturerTypeShortDsc;

        private String manufacturerTypeSourceId;

        private String offerEffectiveDayFridayInd;

        private String offerEffectiveDayMondayInd;

        private String offerEffectiveDaySaturdayInd;

        private String offerEffectiveDaySundayInd;

        private String offerEffectiveDayThursdayInd;

        private String offerEffectiveDayTuesdayInd;

        private String offerEffectiveDayWednesdayInd;

        private String offerEffectiveEndTm;

        private String offerEffectiveStartTm;

        private String offerEffectiveTimeZoneCd;

        private String offerEndDt;

        private String offerItemDsc;

        private String offerItemSizeDsc;

        private String offerNm;

        private String offerRequestCommentTxt;

        private String offerRequestDepartmentNm;

        private String offerRequestDsc;

        private String offerRequestId;

        private String offerRequestTypeCd;

        private String offerStartDt;

        private String allocationTypeCd;

        private String allocationTypeDesc;

        private String allocationTypeShortDesc;

        private String productQty;

        private String promotionProgramTypeCd;

        private String promotionProgramTypeNm;

        private String savingsValueTxt;

        private String sourceSystemId;

        private String storeGroupQty;

        private String testEndDt;

        private String testStartDt;

        private String tierQty;

        private String triggerId;

        private String updatedApplicationId;

        private String chargebackDepartmentId;

        private String versionQty;

    }

    class OfferRequestAirMileTier {

        private String airMilePointQty;

        private String airMileTierNm;

        private String buyProductGroupNm;

        private String discountVersionId;

        private String offerRequestId;

        private String productGroupId;

        private String productGroupVersionId;

        private String storeGroupVersionId;

        private String userInterfaceUniqueId;

    }

    class OfferRequestChangeDetail {

        private String changeTypeCd      ;

        private String changeTypeDsc     ;

        private String changeTypeQty     ;

        private String changeCategoryCd  ;

        private String changeCategoryDsc ;

        private String changeCategoryQty ;

        private String reasonTypeCd     ;

        private String reasonTypeDsc    ;

        private String reasonCommentTxt ;

    }

    class OfferRequestDiscountTier {

        private String buyProductGroupNm;

        private String discountAmt;

        private String discountId;

        private String discountUpToQty;

        private String discountVersionId;

        private String limitAmt;

        private String limitQty;

        private String limitVol;

        private String limitWt;

        private String offerRequestId;

        private String productGroupId;

        private String productGroupVersionId;

        private String receiptTxt;

        private String rewardQty;

        private String storeGroupVersionId;

        private String tierLevelNbr;

        private String unitOfMeasureCd;

        private String unitOfMeasureNm;

        private String userInterfaceUniqueId;

    }

    class OfferRequestDiscountVersionDiscount {

        private String benefitValueQty;

        private String benefitValueTypeCode;

        private String benefitValueTypeDsc;

        private String benefitValueTypeShortDsc;

        private String buyProductGroupNm;

        private String chargebackDepartmentId;

        private String chargebackDepartmentNm;

        private String discountId;

        private String discountTypeCd;

        private String discountTypeDsc;

        private String discountTypeShortDsc;

        private String discountVersionId;

        private String excludedProductGroupId;

        private String excludedProductGroupNm;

        private String includedProductGroupId;

        private String includedProductGroupNm;

        private String offerRequestId;

        private String productGroupId;

        private String productGroupVersionId;

        private String storeGroupVersionId;

        private String userInterfaceUniqueId;

    }

    class OfferRequestGetDiscountVersion {

        private String airMileProgramId;

        private String airMileProgramNm;

        private String buyProductGroupNm;

        private String discountVersionId;

        private String offerRequestId;

        private String productGroupId;

        private String productGroupVersionId;

        private String storeGroupVersionId;

        private String userInterfaceUniqueId;

    }

    class OfferRequestGroup {

        private String groupCd;

        private String offerRequestId;

        private String groupId;

        private String groupNm;

    }

    class OfferRequestOffer {

        private String appliedProgramNm;

        private String attachedOfferStatusDsc;

        private String attachedOfferStatusEffectiveTs;

        private String attachedOfferStatusTypeCd;

        private String copientOfferId;

        private String discountId;

        private String discountVersionId;

        private String distinctId;

        private String offerExternalId;

        private String offerId;

        private String offerRequestId;

        private String productGroupVersionId;

        private String programAppliedInd;

        private String storeGroupVersionId;

        private String userInterfaceUniqueId ;

    }

    class OfferRequestOfferSpecification {

        private String displayOrderNbr;

        private String instantWinFrequencyDsc;

        private String instantWinPrizeItemQty;

        private String instantWinProgramId;

        private String instantWinVersionId;

        private String loyaltyProgramTagInd;

        private String offerRequestId;

        private String podCustomerFriendlyCategoryCd;

        private String podCustomerFriendlyCategoryDsc;

        private String podCustomerFriendlyCategoryShortDsc;

        private String podDisclaimerTxt;

        private String podDisplayEndDt;

        private String podDisplayStartDt;

        private String podHeadlineTxt;

        private String podOfferDsc;

        private String podPriceInfoTxt;

        private String protoTypeCd;

        private String protoTypeDsc;

        private String protoTypeShortDsc;

        private String storeGroupVersionId;

        private String storeTagAmt;

        private String storeTagDsc;

        private String storeTagNbr;

        private String userInterfaceUniqueId;

        private String podHeadlineSubTxt;

        private String podOfferDetailCd;

        private String podOfferDetailDsc;

        private String podOfferDetailShortDsc;

        private String podItemQty;

        private String podUnitOfMeasureCd;

        private String podUnitOfMeasureNm;

        private String usageLimitTypeTxt;

    }

    class OfferRequestPodCategory {

        private String offerRequestId;

        private String userInterfaceUniqueId;

        private String podCategoryCd;

        private String podCategoryDsc;

        private String podCategoryShortDsc;

    }

    class OfferRequestPodDisplayImage {

        private String offerRequestId;

        private String podImageId;

        private String podImageTypeCd;

        private String userInterfaceUniqueId;

    }

    class OfferRequestPodSpecialEvent {

        private String offerRequestId;

        private String podEventId;

        private String podEventNm;

        private String userInterfaceUniqueId;

    }

    class OfferRequestProductGroup {

        private String anyProductInd;

        private String conjunctionDsc;

        private String displayOrderNbr;

        private String excludedProductGroupId;

        private String excludedProductGroupNm;

        private String corporateItemCd;

        private String representativeUpcCd;

        private String representativeUpcNbr;

        private String representativeUpcTxt;

        private String representativeUpcDsc;

        private String representativeStatusTypeCd;

        private String representativeStatusTypeDsc;

        private String representativeStatusTypeEffectiveTs;

        private String representativeStatusTypeEffectiveEndDt;

        private String statusReasonCd;

        private String statusReasonDsc;

        private String statusReasonShortDsc;

        private String itemOfferUomCd;

        private String itemOfferUomNm;

        private String itemOfferEffectiveStartDt;

        private String itemOfferEffectiveEndDt;

        private String storeGroupVersionId;

        private String uniqueItemInd;

        private String unitOfMeasureCd;

        private String unitOfMeasureDsc;

        private String productGroupId;

        private String unitOfMeasureNm;

        private String userInterfaceUniqueId;

    }

    class OfferRequestProductGroupTier {

        private String offerRequestId;

        private String productGroupId;

        private String productGroupNm;

        private String productGroupVersionId;

        private String tierLevelAmt;

        private String tierLevelId;

        private String userInterfaceUniqueId;

    }

    class OfferRequestPromotionPeriodType {

        private String promotionPeriodId;

        private String offerRequestId;

        private String promotionPeriodNm;

        private String promotionWeekId;

        private String promotionStartDt;

        private String promotionEndDt;

    }

    class OfferRequestPromotionProgramType {

        private String offerRequestId;

        private String programTypeCd;

        private String programTypeNm;

        private String programSubTypeCd;

        private String programSubTypeNm;

    }

    class OfferRequestReference {

        private String offerRequestId;

        private String offerRequestReferenceId;

    }

    class OfferRequestRegion {

        private String offerRequestId;

        private String regionId;

        private String regionNm;

    }

    class OfferRequestRequirementType {

        private String offerRequestId;

        private String requirementTypeCd;

        private String requiredQty;

        private String requiredInd;

    }

    class OfferRequestRestrictionType {

        private String limitAmt;

        private String limitQty;

        private String limitVol;

        private String limitWt;

        private String offerRequestId;

        private String unitOfMeasureCd;

        private String unitOfMeasureNm;

        private String restrictionTypeShortDsc;

        private String restrictionTypeDsc;

        private String usageLimitTypeTxt;

        private String usageLimitNbr;

        private String restrictionTypeCd;

        //private String usageLimitTypeTxt;

    }

    class OfferRequestShoppingListCategory {

        private String offerRequestId;

        private String userInterfaceUniqueId;

        private String shoppingListCategoryCd;

        private String shoppingListCategoryDsc;

        private String shoppingListCategoryShortDsc;

    }

    class OfferRequestStatus {

        private String effectiveTs;

        private String offerRequestId;

        private String offerRequestStatusCd;

        private String offerRequestStatusDsc;

        private String offerRequestStatusTypeCd;

    }

    class OfferRequestStoreGroup {

        private String offerRequestId;

        private String storeGroupDsc;

        private String storeGroupId;

        private String storeGroupNm;

        private String storeGroupTypeCd;

        private String storeGroupTypeDsc;

        private String storeGroupTypeShortDsc;

        private String userInterfaceUniqueId;

    }

    class OfferRequestSubGroup {

        private String groupId;

        private String subGroupCd;

        private String subGroupId;

        private String subGroupNm;

    }

    class OfferRequestUserUpdate {

        private String createTs;

        private String offerRequestId;

        private String updateTs;

        private String userFirstNm;

        private String userId;

        private String userLastNm;

        private String userTypeCd;

    }

    class OfferRequestVendorPromotion {

        private String offerRequestId;

        private String vendorPromotionId;

    }

}
