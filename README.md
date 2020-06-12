# Nerde Yesem

Nerde Yesem, kullanıcının bulunduğu lokasyona en yakın restoranları listeler. Seçilen restoran hakkında detaylı bilgileri kullanıcı seçimine göre gösterir. 

## Hakkında

Veriler [Zomato](https://developers.zomato.com/api?lang=tr) API'sinden çekilir. Uyguluma MVVM mimarisinde geliştirilmiştir. 
>MVVM = Model + View + View Model 

## Ekranlar
![Ekranlar](https://user-images.githubusercontent.com/45212967/84511169-cfeede00-acce-11ea-8d2a-7e3c143c58fd.png)

### Planlama

- [x] Parmak izi ile giriş eklendi
- [x] Background servis ile kullanıcı konum bilgisi alma eklendi
- [x] Retrofit - RxJava entegarasyonu yapıldı
- [x] Kullanıcıya yakın konumda bulunan restoranları listeleme tamamlandı
- [x] Seçilen restoranları başka bir sayfada detaylıca gösterme eklendi
- [x] Geri tuşuna basıldığı zaman, bir önceki sayfaya gitme eklendi
- [x] Restoran paylaşma eklendi(fotoğraf paylaşımını destekleyen tüm uygulamalarda paylaşılabilir)
- [x] Cihazın internet bağlantısı kontrolü eklendi.

### Kullanılan teknolojiler
 - Retrofit
 - RxJava
 - Broadcast Manager
 - Android Background Service
 - Livedata
 - ViewModel
 - Handler - Runnable