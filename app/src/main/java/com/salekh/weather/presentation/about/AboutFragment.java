package com.salekh.weather.presentation.about;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.DialogFragment;

import com.salekh.weather.R;
import com.salekh.weather.databinding.FragmentAboutBinding;
import com.salekh.weather.presentation.main.MainActivity;
import com.salekh.weather.utils.AppUtil;
import com.salekh.weather.utils.LocaleManager;
import com.salekh.weather.utils.MyApplication;
import com.salekh.weather.utils.SharedPreferencesUtil;
import com.salekh.weather.utils.ViewAnimation;


public class AboutFragment extends DialogFragment {

  private Activity activity;
  private String currentLanguage;
  private FragmentAboutBinding binding;

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    binding = FragmentAboutBinding.inflate(inflater, container, false);
    View view = binding.getRoot();
    initVariables(view);
    return view;
  }

  private void initVariables(View view) {
    currentLanguage = MyApplication.localeManager.getLanguage();
    activity = getActivity();
    if (activity != null) {
      String versionName = "";
      try {
        versionName = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0).versionName;
      } catch (PackageManager.NameNotFoundException e) {
        // do nothing
      }
      setTextWithLinks(view.findViewById(R.id.text_application_info), getString(R.string.application_info_text, versionName));
      setTextWithLinks(view.findViewById(R.id.text_developer_info), getString(R.string.developer_info_text));
      setTextWithLinks(view.findViewById(R.id.text_design_api), getString(R.string.design_api_text));
      setTextWithLinks(view.findViewById(R.id.text_libraries), getString(R.string.libraries_text));
      setTextWithLinks(view.findViewById(R.id.text_license), getString(R.string.license_text));
      
      int chipId = getChipIdForLanguage(currentLanguage);
      if (chipId != -1) {
        binding.languageChipGroup.check(chipId);
      }
    }
    binding.languageChipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
      if (!checkedIds.isEmpty()) {
        int checkedId = checkedIds.get(0);
        String selectedLanguage = getLanguageForChipId(checkedId);
        if (selectedLanguage != null && !selectedLanguage.equals(currentLanguage)) {
          MyApplication.localeManager.setNewLocale(activity, selectedLanguage);
          restartActivity();
        }
      }
    });
    binding.nightModeSwitch.setChecked(SharedPreferencesUtil.getInstance(activity).isDarkThemeEnabled());
    binding.nightModeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        SharedPreferencesUtil.getInstance(activity).setDarkThemeEnabled(isChecked);
        if (isChecked) {
          AppCompatDelegate.setDefaultNightMode(
              AppCompatDelegate.MODE_NIGHT_YES);
        } else {
          AppCompatDelegate.setDefaultNightMode(
              AppCompatDelegate.MODE_NIGHT_NO);
        }
        activity.recreate();
      }
    });
    binding.closeButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        dismiss();
        if (getFragmentManager() != null) {
          getFragmentManager().popBackStack();
        }
      }
    });

    binding.toggleInfoButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        toggleView();
      }
    });
    binding.toggleInfoLayout.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        toggleView();
      }
    });
  }

  private void toggleView() {
    boolean show = toggleArrow(binding.toggleInfoButton);
    if (show) {
      ViewAnimation.expand(binding.expandLayout, new ViewAnimation.AnimListener() {
        @Override
        public void onFinish() {
        }
      });
    } else {
      ViewAnimation.collapse(binding.expandLayout);
    }
  }

  private void setTextWithLinks(TextView textView, String htmlText) {
    AppUtil.setTextWithLinks(textView, AppUtil.fromHtml(htmlText));
  }


  @NonNull
  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    Dialog dialog = super.onCreateDialog(savedInstanceState);
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
    dialog.setCancelable(true);
    WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
    lp.copyFrom(dialog.getWindow().getAttributes());
    lp.width = WindowManager.LayoutParams.MATCH_PARENT;
    lp.height = WindowManager.LayoutParams.MATCH_PARENT;
    dialog.getWindow().setAttributes(lp);
    return dialog;
  }

  private int getChipIdForLanguage(String language) {
    if (LocaleManager.LANGUAGE_ENGLISH.equals(language)) return R.id.chip_en;
    if (LocaleManager.LANGUAGE_PERSIAN.equals(language)) return R.id.chip_fa;
    if (LocaleManager.LANGUAGE_SPANISH.equals(language)) return R.id.chip_es;
    if (LocaleManager.LANGUAGE_FRENCH.equals(language)) return R.id.chip_fr;
    if (LocaleManager.LANGUAGE_ARABIC.equals(language)) return R.id.chip_ar;
    if (LocaleManager.LANGUAGE_CHINESE.equals(language)) return R.id.chip_zh;
    if (LocaleManager.LANGUAGE_HINDI.equals(language)) return R.id.chip_hi;
    if (LocaleManager.LANGUAGE_GEORGIAN.equals(language)) return R.id.chip_ka;
    if (LocaleManager.LANGUAGE_AZERBAIJANI.equals(language)) return R.id.chip_az;
    if (LocaleManager.LANGUAGE_TURKISH.equals(language)) return R.id.chip_tr;
    return -1;
  }

  private String getLanguageForChipId(int chipId) {
    if (chipId == R.id.chip_en) return LocaleManager.LANGUAGE_ENGLISH;
    if (chipId == R.id.chip_fa) return LocaleManager.LANGUAGE_PERSIAN;
    if (chipId == R.id.chip_es) return LocaleManager.LANGUAGE_SPANISH;
    if (chipId == R.id.chip_fr) return LocaleManager.LANGUAGE_FRENCH;
    if (chipId == R.id.chip_ar) return LocaleManager.LANGUAGE_ARABIC;
    if (chipId == R.id.chip_zh) return LocaleManager.LANGUAGE_CHINESE;
    if (chipId == R.id.chip_hi) return LocaleManager.LANGUAGE_HINDI;
    if (chipId == R.id.chip_ka) return LocaleManager.LANGUAGE_GEORGIAN;
    if (chipId == R.id.chip_az) return LocaleManager.LANGUAGE_AZERBAIJANI;
    if (chipId == R.id.chip_tr) return LocaleManager.LANGUAGE_TURKISH;
    return null;
  }

  private void restartActivity() {
    Intent intent = new Intent(activity, MainActivity.class);
    activity.startActivity(intent);
    activity.finish();
  }

  private boolean toggleArrow(View view) {
    if (view.getRotation() == 0) {
      view.animate().setDuration(200).rotation(180);
      return true;
    } else {
      view.animate().setDuration(200).rotation(0);
      return false;
    }
  }
}
