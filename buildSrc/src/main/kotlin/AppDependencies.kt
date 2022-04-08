object AppDependencies {
    //security
    private const val encryptSharedPref =
        "androidx.security:security-crypto:${Versions.securityShared}"

    // Retrofit
    private const val retrofit = "com.squareup.retrofit2:retrofit:${Versions.retrofit}"
    private const val retrofitJsonAdapter =
        "com.squareup.retrofit2:converter-moshi:${Versions.retrofit}"
    private const val retrofitRxJavaAdapter =
        "com.squareup.retrofit2:adapter-rxjava3:${Versions.retrofit}"
    private const val retrofitInterceptor =
        "com.squareup.okhttp3:logging-interceptor:${Versions.okHttp}"

    //ViewModel
    private const val liveData = "androidx.lifecycle:lifecycle-livedata-ktx:${Versions.viewModel}"
    private const val viewModel = "androidx.lifecycle:lifecycle-livedata-ktx:${Versions.viewModel}"

    //Navigation
    private const val navFragment = "androidx.navigation:navigation-fragment-ktx:${Versions.navigation}"
    private const val navKtx = "androidx.navigation:navigation-ui-ktx:${Versions.navigation}"

    //FireBase
    private const val fireBaseMessaging = "com.google.firebase:firebase-messaging:${Versions.fireBase}"
    private const val fireBaseMessagingKtx = "com.google.firebase:firebase-messaging-ktx:${Versions.fireBase}"

    //Room
    private const val runtimeRoom = "androidx.room:room-runtime:${Versions.runtime}"
    private const val compilerRoom = "androidx.room:room-compiler:${Versions.roomCompiler}"
    private const val roomKtxRoom = "androidx.room:room-ktx:${Versions.roomKtx}"

    //RxJava3
    private const val rxJavaAndroid = "io.reactivex.rxjava3:rxandroid:${Versions.rxAndroid}"
    private const val rxJava = "io.reactivex.rxjava3:rxjava:${Versions.rxJava}"

    // Dagger
    private const val daggerAndroid = "com.google.dagger:dagger-android:${Versions.dagger}"
    private const val daggerAndroidSupport = "com.google.dagger:dagger-android-support:${Versions.dagger}"
    private const val daggerKaptProcessor = "com.google.dagger:dagger-android-processor:${Versions.kaptProcessor}"
    private const val daggerKaptCompiler = "com.google.dagger:dagger-compiler:${Versions.kaptCompiler}"

    // Adapter Delegate
    private const val  adapterDelegateLayount = "com.hannesdorfmann:adapterdelegates4-kotlin-dsl-layoutcontainer:${Versions.adapter}"
    private const val  adapterDelegateViewBinding = "com.hannesdorfmann:adapterdelegates4-kotlin-dsl-viewbinding:${Versions.adapter}"
    private const val  adapterDelegate = "com.hannesdorfmann:adapterdelegates4-kotlin-dsl:${Versions.adapter}"

    //UI
    private const val activity = "androidx.activity:activity-ktx:${Versions.activity}"
    private const val coil = "io.coil-kt:coil:${Versions.coil}"
    private const val kotlinKtx = "androidx.core:core-ktx:${Versions.kotlinKtx}"
    private const val appcompat = "androidx.appcompat:appcompat:${Versions.appcompat}"
    private const val material = "com.google.android.material:material:${Versions.material}"
    private const val constraint = "androidx.constraintlayout:constraintlayout:${Versions.constraint}"
    private const val support = "androidx.legacy:legacy-support-v4:${Versions.support}"
    private const val swipe = "androidx.swiperefreshlayout:swiperefreshlayout:${Versions.swipe}"
    private const val recyclerView = "androidx.recyclerview:recyclerview:${Versions.recyclerView}"
    private const val map = "com.yandex.android:maps.mobile:${Versions.map}"

    //Test
    private const val junit = "junit:junit:${Versions.junit}"
    private const val extJunit = "androidx.test.ext:junit:${Versions.extJunit}"
    private const val espresso = "androidx.test.espresso:espresso-core:${Versions.espresso}"


    val ui = arrayListOf(
        activity,
        kotlinKtx,
        support,
        coil,
        appcompat,
        material,
        constraint,
        swipe,
        recyclerView
    )

    val navImpl = arrayListOf(
        navFragment,
        navKtx
    )

    val fireBaseImpl = arrayListOf(
        fireBaseMessaging,
        fireBaseMessagingKtx
    )

    val rxJavaImpl = arrayListOf(
        rxJava,
        rxJavaAndroid
    )

    val diImpl = arrayListOf(
        daggerAndroid,
        daggerAndroidSupport
    )

    val retrofitImpl = arrayListOf(
        retrofit,
        retrofitJsonAdapter,
        retrofitInterceptor,
        retrofitRxJavaAdapter
    )

    val viewModelImpl = arrayListOf(
        liveData,
        viewModel
    )

    val recyclerViewAdapterImpl = arrayListOf(
        adapterDelegateLayount,
        adapterDelegateViewBinding,
        adapterDelegate
    )

    val roomImpl = arrayListOf(
        runtimeRoom,
        roomKtxRoom
    )

    val security = arrayListOf(
        encryptSharedPref
    )

    val kapt = arrayListOf(
        compilerRoom,
        daggerKaptCompiler,
        daggerKaptProcessor
    )

    val androidTestLibraries = arrayListOf(espresso, extJunit)

    val testLibraries = arrayListOf(junit)
}