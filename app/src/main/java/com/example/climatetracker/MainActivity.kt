package com.example.climatetracker

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.SearchView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.climatetracker.databinding.ActivityMainBinding
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")

    private lateinit var binding: ActivityMainBinding

    //10858614dbacba79165cd0b777573a4e - API KEY
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.searchView.setOnSearchClickListener {
            binding.location.visibility = View.GONE
            binding.time.visibility = View.GONE
        }

        binding.searchView.setOnCloseListener {
            // This method will be called when the search view is closed
            // You can perform actions here

            // Show text1 and text2 again
            binding.location.visibility = View.VISIBLE
            binding.time.visibility = View.VISIBLE

            // Return false if you want to allow the default behavior (closing the search view)
            // Return true if you want to consume the event and prevent the search view from closing
            false
        }

        fetchWeatherData("Kalyani")

        searchCity()


    }

    private fun searchCity() {
        val searchView = binding.searchView
        searchView.setOnQueryTextListener(object :SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {
                if (p0 != null) {
                    fetchWeatherData(p0)
                }
                return true
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                return true
            }

        })
    }


    private fun fetchWeatherData(CityName:String) {
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build().create(GetInterface::class.java)
        val response = retrofit.getWeatherData(CityName,"10858614dbacba79165cd0b777573a4e","metric")
        response.enqueue(object : Callback<WeatherApp>{
            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call<WeatherApp>, response: Response<WeatherApp>) {
                val responseBody = response.body()
                if(response.isSuccessful && responseBody!=null){
                    val temperature = responseBody.main.temp.toInt()
                    val CityName = responseBody.name
                    val country = responseBody.sys.country
                    val humidity = responseBody.main.humidity.toString()
                    val sea = responseBody.main.pressure.toString()
                    val speed = responseBody.wind.speed.toString()
                    val condition = responseBody.weather.firstOrNull()?.main?:"unknown"
                    val sunrise = responseBody.sys.sunrise.toLong()
                    val sunset = responseBody.sys.sunset.toLong()
                    val maxtemp = responseBody.main.temp_max.toInt()
                    val mintemp = responseBody.main.temp_min.toInt()
                    val month = date()
                        val day = dayName(System.currentTimeMillis())


                    binding.temp.text= "$temperature°C"
                    binding.location.text="$CityName, $country"
                    binding.humidity.text="$humidity%"
                    binding.sea.text="$sea hpa"
                    binding.windspeed.text="$speed m/s"
                    binding.condition2.text=condition
                    binding.sunrise.text="${time(sunrise)}"
                    binding.sunset.text="${time(sunset)}"
                    binding.condition.text=condition
                    binding.maxt.text= "Max : $maxtemp°C"
                    binding.mint.text="Min  : $mintemp°C"
                    binding.day.text=day
                    binding.time.text=month


                    changePic(condition)
                }
            }

            override fun onFailure(call: Call<WeatherApp>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun changePic(conditions : String) {
        when(conditions){
            "Overcast","Partly Clouds","Clouds","Mist","Foggy"->{
                binding.MainLogo.setAnimation(R.raw.cloudjoy)
            }
            "Clear Sky","Sunny","Clear"->{
                binding.MainLogo.setAnimation(R.raw.sunnyweather)
            }
            "Light Rain","Drizzle","Moderate Rain","Showers","Heavy Rain"->{
                binding.MainLogo.setAnimation(R.raw.rainjoy)
            }
            "Light Snow","Moderate Snow","Heavy Snow","Blizzard"->{
                binding.MainLogo.setAnimation(R.raw.snow)
            }
            else->{
                binding.MainLogo.setAnimation(R.raw.cloudjoy)
            }

        }
        binding.MainLogo.playAnimation()
    }

    private fun date(): String {
        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return sdf.format((Date()))
    }
    private fun time(timestamp: Long): String {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format((Date(timestamp*1000)))
    }

    fun dayName(timestamp: Long): String {
        val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format((Date()))
    }
}


