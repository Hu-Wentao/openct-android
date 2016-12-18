package cc.metapro.openct.gradelist;


import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.base.Strings;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cc.metapro.openct.R;
import cc.metapro.openct.data.GradeInfo;
import cc.metapro.openct.utils.ActivityUtils;
import cc.metapro.openct.utils.Constants;
import cc.metapro.openct.utils.RecyclerViewHelper;

public class GradeFragment extends Fragment implements GradeContract.View {

    private GradeAdapter mGradeAdapter;

    private GradeContract.Presenter mPresenter;

    private AppCompatTextView mCAPTCHA;

    private AlertDialog.Builder ab;

    public GradeFragment() {
    }

    public static GradeFragment newInstance() {
        return new GradeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_grade, container, false);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.grade_recycler_view);
        mGradeAdapter = new GradeAdapter(getContext());
        RecyclerViewHelper.setRecyclerView(getContext(), recyclerView, mGradeAdapter);
        return view;
    }

    @Override
    public void onResume() {
        mPresenter.loadLocalGradeInfos(getContext());
        super.onResume();
    }

    @Override
    public void onDestroy() {
        mPresenter.storeGradeInfos(getContext());
        super.onDestroy();
    }

    @Override
    public void showAll(List<GradeInfo> infos) {
        mGradeAdapter.setNewGradeInfos(infos);
        mGradeAdapter.notifyDataSetChanged();
    }

    @Override
    public void showOnResultFail() {
        ActivityUtils.dismissProgressDialog();
        Snackbar.make(getView(), "还没有这个学期的成绩", Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void showOnResultOk() {
        ActivityUtils.dismissProgressDialog();
    }

    @Override
    public void showOnCodeEmpty() {
        Toast.makeText(getContext(), "请输入验证码", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setCAPTCHADialogHelper(ActivityUtils.CaptchaDialogHelper captchaDialogHelper) {
        mCAPTCHA = captchaDialogHelper.getCAPTCHATextView();
    }

    @Override
    public void showOnCAPTCHALoaded(Drawable captcha) {
        mCAPTCHA.setText("");
        mCAPTCHA.setBackgroundDrawable(captcha);
    }

    @Override
    public void showOnCAPTCHAFail() {
        Toast.makeText(getContext(), "获取验证码失败, 再试一次", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showCETQueryDialog() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.cet_query_dialog, null);
        final EditText num = (EditText) view.findViewById(R.id.cet_cert_num);
        final EditText name = (EditText) view.findViewById(R.id.cet_cert_name);

        ab = new AlertDialog.Builder(getContext());
        ab.setPositiveButton("查询", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String n = num.getText().toString();
                String na = name.getText().toString();
                if (!Strings.isNullOrEmpty(n) && !Strings.isNullOrEmpty(na)) {
                    Map<String, String> queryMap = new HashMap<>(2);
                    queryMap.put(Constants.CET_NUM_KEY, n);
                    queryMap.put(Constants.CET_NAME_KEY, na);
                    mPresenter.loadCETGradeInfos(queryMap);
                    ActivityUtils.getProgressDialog(getContext(), null, "正在加载CET成绩").show();
                }
            }
        });
        ab.setTitle("CET 成绩查询");
        ab.setNegativeButton("取消", null);
        ab.setCancelable(false);
        ab.setView(view);
        ab.show();
    }

    @Override
    public void showCETGrade(Map<String, String> resultMap) {
        String name = resultMap.get(Constants.CET_NAME_KEY);
        String school = resultMap.get(Constants.CET_SCHOOL_KEY);
        String type = resultMap.get(Constants.CET_TYPE_KEY);
        String num = resultMap.get(Constants.CET_NUM_KEY);
        String time = resultMap.get(Constants.CET_TIME_KEY);
        String grade = resultMap.get(Constants.CET_GRADE_KEY);

        View view = LayoutInflater.from(getContext()).inflate(R.layout.cet_result_dialog, null);
        LinearLayout layout = (LinearLayout) view.findViewById(R.id.cet_result_layout);
        if (!Strings.isNullOrEmpty(name)) {
            TextView textView = new TextView(getContext());
            textView.setText("姓名: " + name);
            textView.setTextSize(15);
            textView.setPadding(10, 10, 10, 10);
            layout.addView(textView);
        }

        if (!Strings.isNullOrEmpty(school)) {
            TextView textView = new TextView(getContext());
            textView.setText("学校: " + school);
            textView.setTextSize(15);
            textView.setPadding(10, 10, 10, 10);
            layout.addView(textView);
        }

        if (!Strings.isNullOrEmpty(type)) {
            TextView textView = new TextView(getContext());
            textView.setText("CET类型: " + type);
            textView.setTextSize(15);
            textView.setPadding(10, 10, 10, 10);
            layout.addView(textView);
        }

        if (!Strings.isNullOrEmpty(num)) {
            TextView textView = new TextView(getContext());
            textView.setText("准考证号: " + num);
            textView.setTextSize(15);
            textView.setPadding(10, 10, 10, 10);
            layout.addView(textView);
        }

        if (!Strings.isNullOrEmpty(time)) {
            TextView textView = new TextView(getContext());
            textView.setText("考试时间: " + time);
            textView.setTextSize(15);
            textView.setPadding(10, 10, 10, 10);
            layout.addView(textView);
        }

        if (!Strings.isNullOrEmpty(grade)) {
            TextView textView = new TextView(getContext());
            textView.setText("成绩: " + grade);
            textView.setTextSize(15);
            textView.setPadding(10, 10, 10, 10);
            layout.addView(textView);
        }

        ab.setPositiveButton("好的", null);
        ab.setNegativeButton("", null);
        ab.setView(view);
        ab.show();
        ActivityUtils.dismissProgressDialog();
    }

    @Override
    public void showOnCETGradeFail() {
        ActivityUtils.dismissProgressDialog();
        Toast.makeText(getContext(), "获取CET成绩失败, 请重试", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setPresenter(GradeContract.Presenter presenter) {
        mPresenter = presenter;
    }
}
