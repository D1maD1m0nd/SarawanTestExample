package ru.sarawan.android.model.data

import com.squareup.moshi.Json

data class MapYandex(

	@Json(name = "response")
	val response: ResponseMap? = null
)

data class Locality(

	@Json(name = "LocalityName")
	val localityName: String? = null,

	@Json(name = "DependentLocality")
	val dependentLocality: DependentLocality? = null,

	@Json(name = "Thoroughfare")
	val thoroughfare: Thoroughfare? = null
)

data class GeoObject(

	@Json(name = "metaDataProperty")
	val metaDataProperty: MetaDataProperty? = null,

	@Json(name = "boundedBy")
	val boundedBy: BoundedBy? = null,

	@Json(name = "name")
	val name: String? = null,

	@Json(name = "Point")
	val point: Point? = null,

	@Json(name = "description")
	val description: String? = null
)

data class AdministrativeArea(

	@Json(name = "AdministrativeAreaName")
	val administrativeAreaName: String? = null,

	@Json(name = "Locality")
	val locality: Locality? = null
)

data class Country(

	@Json(name = "CountryName")
	val countryName: String? = null,

	@Json(name = "AddressLine")
	val addressLine: String? = null,

	@Json(name = "CountryNameCode")
	val countryNameCode: String? = null,

	@Json(name = "AdministrativeArea")
	val administrativeArea: AdministrativeArea? = null
)

data class BoundedBy(

	@Json(name = "Envelope")
	val envelope: Envelope? = null
)

data class Thoroughfare(

	@Json(name = "ThoroughfareName")
	val thoroughfareName: String? = null,

	@Json(name = "Premise")
	val premise: Premise? = null
)

data class PostalCode(

	@Json(name = "PostalCodeNumber")
	val postalCodeNumber: String? = null
)

data class Premise(

	@Json(name = "PremiseNumber")
	val premiseNumber: String? = null,

	@Json(name = "PostalCode")
	val postalCode: PostalCode? = null
)

data class GeoObjectCollection(

	@Json(name = "metaDataProperty")
	val metaDataProperty: MetaDataProperty? = null,

	@Json(name = "featureMember")
	val featureMember: List<FeatureMemberItem?>? = null
)

data class Point(

	@Json(name = "pos")
	val pos: String? = null
)

data class ComponentsItem(

	@Json(name = "kind")
	val kind: String? = null,

	@Json(name = "name")
	val name: String? = null
)

data class FeatureMemberItem(

	@Json(name = "GeoObject")
	val geoObject: GeoObject? = null
)

data class ResponseMap(

	@Json(name = "GeoObjectCollection")
	val geoObjectCollection: GeoObjectCollection? = null
)

data class MetaDataProperty(

	@Json(name = "GeocoderResponseMetaData")
	val geocoderResponseMetaData: GeocoderResponseMetaData? = null,

	@Json(name = "GeocoderMetaData")
	val geocoderMetaData: GeocoderMetaData? = null
)

data class AddressDetails(

	@Json(name = "Country")
	val country: Country? = null
)

data class AddressMap(

	@Json(name = "Components")
	val components: List<ComponentsItem?>? = null,

	@Json(name = "country_code")
	val countryCode: String? = null,

	@Json(name = "formatted")
	val formatted: String? = null,

	@Json(name = "postal_code")
	val postalCode: String? = null
)

data class GeocoderResponseMetaData(

	@Json(name = "request")
	val request: String? = null,

	@Json(name = "found")
	val found: String? = null,

	@Json(name = "Point")
	val point: Point? = null,

	@Json(name = "results")
	val results: String? = null
)

data class GeocoderMetaData(

	@Json(name = "Address")
	val address: AddressMap? = null,

	@Json(name = "AddressDetails")
	val addressDetails: AddressDetails? = null,

	@Json(name = "kind")
	val kind: String? = null,

	@Json(name = "precision")
	val precision: String? = null,

	@Json(name = "text")
	val text: String? = null
)

data class Envelope(

	@Json(name = "lowerCorner")
	val lowerCorner: String? = null,

	@Json(name = "upperCorner")
	val upperCorner: String? = null
)

data class DependentLocality(

	@Json(name = "DependentLocalityName")
	val dependentLocalityName: String? = null,

	@Json(name = "DependentLocality")
	val dependentLocality: DependentLocality? = null
)
