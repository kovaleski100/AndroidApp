package com.example.skysys.map

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.MutableLiveData
import com.example.skysys.R
import com.mapbox.geojson.Point
import com.mapbox.maps.MapView
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.*
import com.mapbox.maps.plugin.gestures.OnMapClickListener
import com.mapbox.maps.plugin.scalebar.scalebar

class Controller{

    private var listOfPointsList = mutableListOf(mutableListOf<Point>())
    private var pointController = 0
    private var drawController = 0
    private lateinit var mapView : MapView
    private lateinit var context : Context
    private var distance :Double = 0.0
    var poligon: MutableLiveData<Int> = MutableLiveData(0)
    private var countList: Int = 0

    fun addList(){
        listOfPointsList.add(mutableListOf<Point>())
    }

    fun getCountList(): Int{
        return this.countList
    }
    fun setCountList(value: Int){
        this.countList = value
    }
    fun setMapView(mapView: MapView)
    {
        this.mapView = mapView
    }

    fun setContext(context: Context)
    {
        this.context = context
    }

    fun setPointController(pointController: Int){
        this.pointController = pointController
    }

    fun setDrawController(drawController: Int)
    {
        this.drawController = drawController
    }

    fun calcDistance(index: Int) :Double
    {
        for(i in 1 .. listOfPointsList[countList].size-1)
        {

            distance += measure(listOfPointsList[index][i-1],listOfPointsList[index][i])
        }
        return distance
    }

    private fun bitmapFromDrawableRes(context: Context, @DrawableRes resourceId: Int) =
        convertDrawableToBitmap(AppCompatResources.getDrawable(context, resourceId))

    private fun convertDrawableToBitmap(sourceDrawable: Drawable?): Bitmap? {
        return if (sourceDrawable is BitmapDrawable) {
            sourceDrawable.bitmap
        } else {
// copying drawable object to not manipulate on the same reference
            val constantState = sourceDrawable?.constantState ?: return null
            val drawable = constantState.newDrawable().mutate()
            val bitmap: Bitmap = Bitmap.createBitmap(
                drawable.intrinsicWidth, drawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            bitmap
        }
    }

    fun setMarker(point: Point)
    {
        bitmapFromDrawableRes(
            context,
            R.mipmap.drag_blue
        )?.let {
            val annotationApi = mapView?.annotations
            val pointAnnotationManager = annotationApi?.createPointAnnotationManager(mapView!!)
            val pointAnnotationOptions: PointAnnotationOptions = PointAnnotationOptions()
                .withPoint(Point.fromLngLat(point.longitude(), point.latitude()))
                .withIconImage(it)
                .withDraggable(true)
            pointAnnotationManager?.create(pointAnnotationOptions)
        }
    }

    fun measure(point1: Point, point2: Point): Double
    {  // generally used geo measurement function
        var R = 6378.137; // Radius of earth in KM
        var dLat = point2.latitude() * Math.PI / 180 - point1.latitude() * Math.PI / 180;
        var dLon = point2.longitude() * Math.PI / 180 - point1.longitude() * Math.PI / 180;
        var a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(point1.latitude() * Math.PI / 180) * Math.cos(point2.latitude() * Math.PI / 180) *
                Math.sin(dLon/2) * Math.sin(dLon/2);
        var c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        var d = R * c;
        return d * 1000; // meters
    }

    fun drawPoligon()
    {
        val annotationApi = mapView?.annotations
        val polygonAnnotationManager = annotationApi?.createPolygonAnnotationManager(mapView)
        val polygonAnnotationOptions: PolygonAnnotationOptions = PolygonAnnotationOptions()
            .withPoints(listOf(listOfPointsList[countList]))
            .withFillColor("#ee4e8b")
            .withFillOpacity(0.4)

        polygonAnnotationManager?.create(polygonAnnotationOptions)
    }
    val mapClick = object : OnMapClickListener {
        override fun onMapClick(point: Point): Boolean {
            var point_var = point
            if(pointController == 1)
            {
                if(drawController == 1 && listOfPointsList[countList].size>2 && measure(listOfPointsList[countList][0],point) <= 40*mapView.scalebar.distancePerPixel)
                {
                    point_var = listOfPointsList[countList][0]
                    drawPoligon()
                    poligon.value = 1
                    setDrawController(0)
                    setPointController(0)
                }
                listOfPointsList[countList].add(point_var)
                drawLine()
                setMarker(point_var)

            }
            return false
        }
    }

    fun drawLine()
    {
        val annotationApi = mapView?.annotations
        val polylineAnnotationManager = annotationApi.createPolylineAnnotationManager(mapView)
        val pointAnnotationOptions = PolylineAnnotationOptions()
            .withPoints(listOfPointsList[countList])
            .withLineColor("#FFFFFF")
            .withLineWidth(5.0)
        polylineAnnotationManager?.create(pointAnnotationOptions)
    }
}