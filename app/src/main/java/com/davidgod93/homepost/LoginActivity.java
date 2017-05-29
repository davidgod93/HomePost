package com.davidgod93.homepost;

import android.annotation.TargetApi;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.davidgod93.objects.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

	/**
	 * Id to identity READ_CONTACTS permission request.
	 */
	private static final int REQUEST_READ_CONTACTS = 0;
	private static final String TAG = "type";
	protected static final int LOGIN_ACT = 0;
	protected static final int REG_USER_ACT = 1;
	protected static final int REG_BUSS_ACT = 2;

	private int type = -1;

	SharedPreferences p;

	// UI references.
	private AutoCompleteTextView mEmailView;
	private EditText mPasswordView;
	private CheckBox cb;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		type = getIntent().getIntExtra(TAG, type);

		// Set up the login form.
		mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
		populateAutoComplete();

		mPasswordView = (EditText) findViewById(R.id.password);
		mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
				if (id == R.id.login || id == EditorInfo.IME_NULL) {
					attemptLogin();
					return true;
				}
				return false;
			}
		});

		cb = (CheckBox) findViewById(R.id.autologin);

		Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
		int r = type > 0 ? R.string.action_register_short : R.string.action_sign_in_short;
		mEmailSignInButton.setText(r);

		p = PreferenceManager.getDefaultSharedPreferences(this);
		boolean b = p.getBoolean("autologin", false);

		if(b) {
			mEmailView.setText(p.getString("mail", ""));
			mPasswordView.setText(p.getString("pass", ""));
			cb.setChecked(true);
			mEmailSignInButton.performClick();
		}
	}

	@Override
	public void onBackPressed() {
		startActivity(new Intent(this, IntroductionActivity.class));
		super.onBackPressed();
	}

	private void populateAutoComplete() {
		if (!mayRequestContacts()) {
			return;
		}

		getLoaderManager().initLoader(0, null, this);
	}

	private boolean mayRequestContacts() {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
			return true;
		}
		if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
			return true;
		}
		if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
			Snackbar.make(mEmailView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
					.setAction(android.R.string.ok, new View.OnClickListener() {
						@Override
						@TargetApi(Build.VERSION_CODES.M)
						public void onClick(View v) {
							requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
						}
					});
		} else {
			requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
		}
		return false;
	}

	/**
	 * Callback received when a permissions request has been completed.
	 */
	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		if (requestCode == REQUEST_READ_CONTACTS) {
			if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				populateAutoComplete();
			}
		}
	}

	public void click(@Nullable  View v) {
		switch (type) {
			case LOGIN_ACT:
				attemptLogin();
				break;
			case REG_BUSS_ACT:
			case REG_USER_ACT:
				attemptRegister(type);
				break;
		}
	}

	/**
	 * Attempts to sign in or register the account specified by the login form.
	 * If there are form errors (invalid email, missing fields, etc.), the
	 * errors are presented and no actual login attempt is made.
	 */
	public void attemptLogin() {

		// Reset errors.
		mEmailView.setError(null);
		mPasswordView.setError(null);

		// Store values at the time of the login attempt.
		String email = mEmailView.getText().toString();
		String password = mPasswordView.getText().toString();

		View v = new View(this);

		boolean cancel = checkForm(email, password, v);

		if(cancel) v.requestFocus();
		else new UserTask(this, email, password, type).start();
	}


	public void attemptRegister(int type) {

		// Reset errors.
		mEmailView.setError(null);
		mPasswordView.setError(null);

		// Store values at the time of the login attempt.
		String email = mEmailView.getText().toString();
		String password = mPasswordView.getText().toString();

		View v = new View(this);

		boolean cancel = checkForm(email, password, v);

		if (cancel) v.requestFocus();
		else new UserTask(this, email, password, type).start();
	}

	@SuppressWarnings({"ParameterCanBeLocal", "UnusedAssignment"})
	private boolean checkForm(String email, String password, View focusView) {
		boolean cancel = false;

		// Check for a valid password, if the user entered one.
		if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
			mPasswordView.setError(getString(R.string.error_invalid_password));
			focusView = mPasswordView;
			cancel = true;
		}

		// Check for a valid email address.
		if (TextUtils.isEmpty(email)) {
			mEmailView.setError(getString(R.string.error_field_required));
			focusView = mEmailView;
			cancel = true;
		} else if (!isEmailValid(email)) {
			mEmailView.setError(getString(R.string.error_invalid_email));
			focusView = mEmailView;
			cancel = true;
		}
		return cancel;
	}

	private boolean isEmailValid(String email) {
		return email.contains("@") && email.contains(".");
	}

	private boolean isPasswordValid(String password) {
		return password.length() > 4;
	}

	@Override
	public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
		return new CursorLoader(this,
				Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI, ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,
				ContactsContract.Contacts.Data.MIMETYPE + " = ?", new String[]{ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE},
				ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
	}

	@Override
	public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
		List<String> emails = new ArrayList<>();
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			emails.add(cursor.getString(ProfileQuery.ADDRESS));
			cursor.moveToNext();
		}
		addEmailsToAutoComplete(emails);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> cursorLoader) {}

	private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
		ArrayAdapter<String> adapter = new ArrayAdapter<>(LoginActivity.this, android.R.layout.simple_dropdown_item_1line, emailAddressCollection);
		mEmailView.setAdapter(adapter);
	}


	private interface ProfileQuery {
		String[] PROJECTION = {
				ContactsContract.CommonDataKinds.Email.ADDRESS,
				ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
		};

		int ADDRESS = 0;
	}

	public void end(boolean status, int type, boolean login, FirebaseUser user, String pass) {
		if (status && login) {
			assert user != null;
			if(cb.isChecked()) {
				SharedPreferences.Editor e = p.edit();
				e.putString("mail", user.getEmail());
				e.putString("pass", pass);
				e.putBoolean("autologin", true);
				e.apply();
			}
			Intent i = new Intent(this, MainMenuActivity.class);
			i.putExtra(User.USER_UID, user.getUid());
			startActivity(i);
			finish();
		} else if (status) {
			assert user != null;
			FirebaseDatabase db = FirebaseDatabase.getInstance();
			DatabaseReference r = db.getReference("users"), u = r.child(user.getUid());
			u.child("mail").setValue(user.getEmail());
			u.child("tipo").setValue(type == REG_USER_ACT ? "usuario" : "chofer");
			u.child("nombre").setValue("Usuario anónimo");
			u.child("imagen").setValue(-1);
			u.child("token").setValue(PreferenceManager.getDefaultSharedPreferences(this).getString("token", null));
			Toast.makeText(this, "Registro completado. Ya puedes iniciar sesión.", Toast.LENGTH_SHORT).show();
			startActivity(new Intent(this, IntroductionActivity.class));
			finish();
		} else if(type == -2) {
			mPasswordView.setError(getString(R.string.error_user_exists));
			mPasswordView.requestFocus();
		} else {
			mPasswordView.setError(getString(R.string.error_incorrect_password));
			mPasswordView.requestFocus();
		}
	}

	public class UserTask extends Thread {

		private final String mEmail;
		private final String mPassword;
		private final LoginActivity i;
		private final int type;

		UserTask(LoginActivity l, String email, String password, int type) {
			i = l;
			mEmail = email;
			mPassword = password;
			this.type = type;
		}

		@Override
		public void run() {
			final FirebaseAuth auth = FirebaseAuth.getInstance();
			switch (type) {
				case LOGIN_ACT:
					auth.signInWithEmailAndPassword(mEmail, mPassword)
							.addOnCompleteListener(i, new OnCompleteListener<AuthResult>() {
								@Override
								public void onComplete(@NonNull final Task<AuthResult> task) {
									runOnUiThread(new Runnable() {
										@Override
										public void run() {
											i.end(task.isSuccessful(), type, true, auth.getCurrentUser(), mPassword);
										}
									});
								}
							});
					break;
				case REG_BUSS_ACT:
					auth.createUserWithEmailAndPassword(mEmail, mPassword)
							.addOnCompleteListener(i, new OnCompleteListener<AuthResult>() {
								@Override
								public void onComplete(@NonNull final Task<AuthResult> task) {
									runOnUiThread(new Runnable() {
										@Override
										public void run() {
											i.end(task.isSuccessful(), type, false, auth.getCurrentUser(), mPassword);
										}
									});
								}
							});
					break;
				case REG_USER_ACT:
					auth.createUserWithEmailAndPassword(mEmail, mPassword)
							.addOnCompleteListener(i, new OnCompleteListener<AuthResult>() {
								@Override
								public void onComplete(@NonNull final Task<AuthResult> task) {
									runOnUiThread(new Runnable() {
										@Override
										public void run() {
											boolean b = task.getException() != null && "The email address is already in use by another account.".equals(task.getException().getMessage());
											i.end(task.isSuccessful(), b ? -2: type, false, auth.getCurrentUser(), mPassword);
										}
									});
								}
							});
					break;
			}
		}
	}
}

